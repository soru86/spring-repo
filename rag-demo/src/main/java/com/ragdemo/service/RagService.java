package com.ragdemo.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RagService {

    private final ChatLanguageModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;
    
    @Value("${ollama.base-url:http://ollama:11434}")
    private String ollamaBaseUrl;
    
    @Value("${ollama.chat-model:llama3.2}")
    private String chatModelName;
    
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String generateResponse(String userMessage, String sessionId) {
        log.info("Generating response for session: {}", sessionId);

        // Generate embedding for user query
        var queryEmbedding = embeddingModel.embed(userMessage).content();

        // Retrieve relevant segments from vector store
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(
                queryEmbedding,
                5 // max results
        );

        // Build context from retrieved segments
        String context = relevantMatches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        log.info("Retrieved {} relevant segments", relevantMatches.size());

        // Build prompt with context
        String prompt = buildPrompt(userMessage, context);

        // Generate response using LLM
        String response = chatModel.generate(prompt);

        // Format the response for better readability
        response = formatResponse(response);

        log.info("Generated response successfully");
        return response;
    }

    public void generateStreamingResponse(String userMessage, String sessionId, Consumer<String> onChunk, Runnable onComplete) {
        log.info("Generating streaming response for session: {}", sessionId);

        // Generate embedding for user query
        var queryEmbedding = embeddingModel.embed(userMessage).content();

        // Retrieve relevant segments from vector store
        List<EmbeddingMatch<TextSegment>> relevantMatches = embeddingStore.findRelevant(
                queryEmbedding,
                5 // max results
        );

        // Build context from retrieved segments
        String context = relevantMatches.stream()
                .map(match -> match.embedded().text())
                .collect(Collectors.joining("\n\n"));

        log.info("Retrieved {} relevant segments", relevantMatches.size());

        // Build prompt with context
        String prompt = buildPrompt(userMessage, context);

        // Generate streaming response using Ollama's streaming API directly
        try {
            URL url = new URL(ollamaBaseUrl + "/api/chat");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(600000);

            // Build request JSON for chat API (uses messages array format)
            // Use ObjectMapper to properly serialize JSON
            com.fasterxml.jackson.databind.node.ObjectNode requestNode = objectMapper.createObjectNode();
            requestNode.put("model", chatModelName);
            requestNode.put("stream", true);
            
            com.fasterxml.jackson.databind.node.ArrayNode messagesArray = objectMapper.createArrayNode();
            com.fasterxml.jackson.databind.node.ObjectNode messageNode = objectMapper.createObjectNode();
            messageNode.put("role", "user");
            messageNode.put("content", prompt);
            messagesArray.add(messageNode);
            requestNode.set("messages", messagesArray);
            
            String requestJson = objectMapper.writeValueAsString(requestNode);

            log.debug("Sending request to Ollama: {}", ollamaBaseUrl + "/api/chat");

            // Send request
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = requestJson.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Check response code
            int responseCode = conn.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                String errorMessage = "HTTP error code: " + responseCode;
                try (BufferedReader errorReader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = errorReader.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorMessage += " - " + errorResponse.toString();
                }
                throw new IOException(errorMessage);
            }

            // Read streaming response
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.trim().isEmpty()) continue;
                    
                    try {
                        JsonNode jsonNode = objectMapper.readTree(line);
                        if (jsonNode.has("message")) {
                            JsonNode msgNode = jsonNode.get("message");
                            if (msgNode.has("content")) {
                                String token = msgNode.get("content").asText();
                                // Preserve all characters including spaces, newlines, etc.
                                if (token != null) {
                                    onChunk.accept(token);
                                }
                            }
                        } else if (jsonNode.has("response")) {
                            // Fallback for generate API format
                            String token = jsonNode.get("response").asText();
                            // Preserve all characters including spaces, newlines, etc.
                            if (token != null) {
                                onChunk.accept(token);
                            }
                        }
                        
                        // Check if done
                        if (jsonNode.has("done") && jsonNode.get("done").asBoolean()) {
                            break;
                        }
                    } catch (Exception jsonException) {
                        log.warn("Failed to parse JSON line: {}", line, jsonException);
                    }
                }
            }

            conn.disconnect();
            log.info("Streaming response completed");
            if (onComplete != null) {
                onComplete.run();
            }
        } catch (Exception e) {
            log.error("Error during streaming", e);
            onChunk.accept("\n\n[Error: " + e.getMessage() + "]");
        }
    }

    private String buildPrompt(String userMessage, String context) {
        return String.format(
                "You are a helpful assistant that answers questions based on the provided context. " +
                "Use the following context to answer the question. If the answer is not in the context, " +
                "say that you don't have enough information.\n\n" +
                "IMPORTANT: Format your response clearly with:\n" +
                "- Proper paragraphs (use line breaks between paragraphs)\n" +
                "- Bullet points or numbered lists when appropriate\n" +
                "- Clear structure and organization\n" +
                "- Use markdown formatting for better readability (use ** for bold, * for italic, etc.)\n\n" +
                "Context:\n%s\n\n" +
                "Question: %s\n\n" +
                "Answer:",
                context,
                userMessage
        );
    }
    
    /**
     * Formats the response to ensure proper line breaks and structure
     */
    private String formatResponse(String response) {
        if (response == null || response.trim().isEmpty()) {
            return response;
        }
        
        // Trim leading/trailing whitespace
        String formatted = response.trim();
        
        // Ensure proper line breaks (replace multiple spaces with single space, except in code blocks)
        formatted = formatted.replaceAll(" +", " ");
        
        // Ensure double line breaks create paragraphs
        formatted = formatted.replaceAll("\n{3,}", "\n\n");
        
        return formatted;
    }
}

