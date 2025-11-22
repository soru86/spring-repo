package com.ragdemo.controller;

import com.ragdemo.dto.ChatRequest;
import com.ragdemo.dto.ChatResponse;
import com.ragdemo.dto.SessionResponse;
import com.ragdemo.entity.ChatMessage;
import com.ragdemo.service.ChatService;
import com.ragdemo.service.RagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final RagService ragService;

    @PostMapping("/message")
    public ResponseEntity<ChatResponse> sendMessage(@RequestBody ChatRequest request) {
        String response = chatService.generateResponse(request.getMessage(), request.getSessionId());
        return ResponseEntity.ok(new ChatResponse(response, request.getSessionId()));
    }

    @GetMapping("/history/{sessionId}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String sessionId) {
        List<ChatMessage> history = chatService.getChatHistory(sessionId);
        return ResponseEntity.ok(history);
    }

    @PostMapping("/session")
    public ResponseEntity<SessionResponse> createSession() {
        String sessionId = chatService.createNewSession();
        return ResponseEntity.ok(new SessionResponse(sessionId));
    }

    @PostMapping(value = "/message/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamMessage(@RequestBody ChatRequest request) {
        SseEmitter emitter = new SseEmitter(600000L); // 10 minute timeout
        
        StringBuilder fullResponse = new StringBuilder();
        
        CompletableFuture.runAsync(() -> {
            try {
                ragService.generateStreamingResponse(
                    request.getMessage(),
                    request.getSessionId(),
                    token -> {
                        try {
                            if (token != null) {
                                fullResponse.append(token);
                                // Send token exactly as received - preserve all formatting
                                emitter.send(SseEmitter.event()
                                    .data(token)
                                    .name("token"));
                            }
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    },
                    () -> {
                        try {
                            // Save to chat history after streaming completes
                            chatService.saveMessage(
                                request.getSessionId(),
                                request.getMessage(),
                                fullResponse.toString()
                            );
                            emitter.send(SseEmitter.event()
                                .data("[DONE]")
                                .name("done"));
                            emitter.complete();
                        } catch (IOException e) {
                            emitter.completeWithError(e);
                        }
                    }
                );
            } catch (Exception e) {
                emitter.completeWithError(e);
            }
        });
        
        emitter.onError(throwable -> {
            // Log error
            System.err.println("SSE Error: " + throwable.getMessage());
        });
        
        emitter.onTimeout(() -> {
            emitter.complete();
        });
        
        return emitter;
    }
}

