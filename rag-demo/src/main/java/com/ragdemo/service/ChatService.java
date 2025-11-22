package com.ragdemo.service;

import com.ragdemo.entity.ChatMessage;
import com.ragdemo.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final RagService ragService;

    public String generateResponse(String userMessage, String sessionId) {
        // Generate response using RAG
        String response = ragService.generateResponse(userMessage, sessionId);

        // Save to chat history
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessage(userMessage);
        chatMessage.setResponse(response);
        chatMessageRepository.save(chatMessage);

        return response;
    }

    public List<ChatMessage> getChatHistory(String sessionId) {
        return chatMessageRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }

    public String createNewSession() {
        return UUID.randomUUID().toString();
    }

    public void saveMessage(String sessionId, String message, String response) {
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setSessionId(sessionId);
        chatMessage.setMessage(message);
        chatMessage.setResponse(response);
        chatMessageRepository.save(chatMessage);
    }
}

