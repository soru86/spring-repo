package com.ragdemo.config;

import dev.langchain4j.model.chat.StreamingChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OllamaConfig {

    @Value("${ollama.base-url:http://ollama:11434}")
    private String ollamaBaseUrl;

    @Value("${ollama.chat-model:llama3.2}")
    private String chatModelName;

    @Value("${ollama.embedding-model:nomic-embed-text}")
    private String embeddingModelName;

    @Value("${ollama.timeout-seconds:600}")
    private Integer timeoutSeconds;

    @Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(chatModelName)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    @Bean
    public OllamaStreamingChatModel ollamaStreamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(chatModelName)
                .temperature(0.7)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .build();
    }

    @Bean
    public OllamaEmbeddingModel ollamaEmbeddingModel() {
        return OllamaEmbeddingModel.builder()
                .baseUrl(ollamaBaseUrl)
                .modelName(embeddingModelName)
                .build();
    }
}

