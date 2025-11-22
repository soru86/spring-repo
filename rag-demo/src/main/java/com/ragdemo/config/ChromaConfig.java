package com.ragdemo.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.chroma.ChromaEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class ChromaConfig {

    @Value("${chroma.base-url:http://chroma:8000}")
    private String chromaBaseUrl;

    @Value("${chroma.collection-name:rag-documents}")
    private String collectionName;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        log.info("Initializing Chroma embedding store at: {}", chromaBaseUrl);
        
        // Retry logic to handle Chroma startup timing
        int maxRetries = 10;
        int retryDelaySeconds = 3;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                // Try to delete existing collection if it exists (to handle dimension mismatches)
                // This will fail silently if collection doesn't exist, which is fine
                try {
                    deleteCollectionIfExists();
                } catch (Exception e) {
                    log.debug("Could not delete collection (may not exist): {}", e.getMessage());
                }
                
                EmbeddingStore<TextSegment> store = ChromaEmbeddingStore.builder()
                        .baseUrl(chromaBaseUrl)
                        .collectionName(collectionName)
                        .build();
                log.info("Successfully connected to Chroma embedding store with collection: {}", collectionName);
                return store;
            } catch (Exception e) {
                // Check if error is due to dimension mismatch
                if (e.getMessage() != null && e.getMessage().contains("dimension")) {
                    log.warn("Dimension mismatch detected. Attempting to delete and recreate collection...");
                    try {
                        deleteCollectionIfExists();
                        // Retry creating the store after deletion
                        EmbeddingStore<TextSegment> store = ChromaEmbeddingStore.builder()
                                .baseUrl(chromaBaseUrl)
                                .collectionName(collectionName)
                                .build();
                        log.info("Successfully recreated Chroma collection with correct dimensions");
                        return store;
                    } catch (Exception retryException) {
                        log.error("Failed to recreate collection after dimension mismatch", retryException);
                        throw new RuntimeException("Failed to initialize Chroma embedding store due to dimension mismatch. " +
                                "Please delete the Chroma volume or collection manually.", retryException);
                    }
                }
                
                if (attempt < maxRetries) {
                    log.warn("Failed to connect to Chroma (attempt {}/{}). Retrying in {} seconds...", 
                            attempt, maxRetries, retryDelaySeconds);
                    try {
                        TimeUnit.SECONDS.sleep(retryDelaySeconds);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new RuntimeException("Interrupted while waiting for Chroma", ie);
                    }
                } else {
                    log.error("Failed to connect to Chroma after {} attempts", maxRetries);
                    throw new RuntimeException("Failed to initialize Chroma embedding store after " + 
                            maxRetries + " attempts. Make sure Chroma is running and accessible at: " + 
                            chromaBaseUrl, e);
                }
            }
        }
        
        throw new RuntimeException("Failed to initialize Chroma embedding store");
    }
    
    private void deleteCollectionIfExists() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String deleteUrl = chromaBaseUrl + "/api/v1/collections/" + collectionName;
        
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(deleteUrl))
                .DELETE()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() == 200 || response.statusCode() == 404) {
            log.info("Collection '{}' deleted or does not exist", collectionName);
        } else {
            log.warn("Unexpected status code when deleting collection: {}", response.statusCode());
        }
    }
}

