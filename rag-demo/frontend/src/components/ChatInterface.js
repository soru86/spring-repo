import React, { useState, useEffect, useRef } from 'react';
import axios from 'axios';
import './ChatInterface.css';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

// Format text with markdown support and line breaks
const formatMessage = (text) => {
  if (!text) return '';

  // First, handle code blocks (preserve them)
  const codeBlockRegex = /```[\s\S]*?```/g;
  const codeBlocks = [];
  let processedText = text.replace(codeBlockRegex, (match) => {
    const id = `__CODE_BLOCK_${codeBlocks.length}__`;
    codeBlocks.push(match);
    return id;
  });

  // Split by double line breaks to create paragraphs
  const paragraphs = processedText.split(/\n\s*\n/);

  const formattedParagraphs = paragraphs.map((paragraph) => {
    // Restore code blocks
    let restoredParagraph = paragraph;
    codeBlocks.forEach((block, index) => {
      restoredParagraph = restoredParagraph.replace(`__CODE_BLOCK_${index}__`, block);
    });

    // Process markdown formatting (order matters - bold before italic)
    let formatted = restoredParagraph
      // Bold: **text** or __text__
      .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
      .replace(/__(.+?)__/g, '<strong>$1</strong>')
      // Code blocks: ```code``` (handle before inline code)
      .replace(/```([\s\S]*?)```/g, '<pre><code>$1</code></pre>')
      // Code: `code` (inline)
      .replace(/`([^`]+)`/g, '<code>$1</code>')
      // Line breaks within paragraph
      .replace(/\n/g, '<br/>');

    // Check if it's a list (starts with - or * or number)
    const trimmedPara = restoredParagraph.trim();
    const isList = /^[\s]*[-*•]\s/.test(trimmedPara) || /^[\s]*\d+\.\s/.test(trimmedPara);

    if (isList) {
      const listItems = restoredParagraph.split(/\n/).filter(item => item.trim());
      const listType = /^[\s]*\d+\.\s/.test(trimmedPara) ? 'ol' : 'ul';
      const listContent = listItems.map(item => {
        const cleanItem = item.replace(/^[\s]*[-*•\d+\.]\s*/, '').trim();
        const formattedItem = cleanItem
          .replace(/\*\*(.+?)\*\*/g, '<strong>$1</strong>')
          .replace(/__(.+?)__/g, '<strong>$1</strong>')
          .replace(/`([^`]+)`/g, '<code>$1</code>');
        return `<li>${formattedItem}</li>`;
      }).join('');
      return `<${listType} class="formatted-list">${listContent}</${listType}>`;
    }

    return `<p class="formatted-paragraph">${formatted}</p>`;
  });

  return formattedParagraphs.join('');
};

function ChatInterface({ sessionId, isLoading }) {
  const [messages, setMessages] = useState([]);
  const [inputMessage, setInputMessage] = useState('');
  const [sending, setSending] = useState(false);
  const messagesEndRef = useRef(null);

  useEffect(() => {
    if (sessionId) {
      loadChatHistory();
    }
  }, [sessionId]);

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  const loadChatHistory = async () => {
    try {
      const response = await axios.get(`${API_BASE_URL}/chat/history/${sessionId}`);
      setMessages(response.data.map(msg => ({
        id: msg.id,
        text: msg.message,
        response: msg.response,
        timestamp: msg.timestamp
      })));
    } catch (error) {
      console.error('Failed to load chat history:', error);
    }
  };

  const handleSend = async () => {
    if (!inputMessage.trim() || !sessionId || sending) return;

    const userMessage = inputMessage.trim();
    setInputMessage('');
    setSending(true);

    // Add user message to UI immediately
    const tempUserMsg = {
      id: Date.now(),
      text: userMessage,
      response: '',
      timestamp: new Date().toISOString()
    };
    setMessages(prev => [...prev, tempUserMsg]);

    try {
      // Use streaming endpoint with EventSource-like parsing
      const response = await fetch(`${API_BASE_URL}/chat/message/stream`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          message: userMessage,
          sessionId: sessionId
        })
      });

      if (!response.ok) {
        throw new Error('Failed to get streaming response');
      }

      const reader = response.body.getReader();
      const decoder = new TextDecoder();
      let buffer = '';

      while (true) {
        const { done, value } = await reader.read();
        
        if (done) {
          // Process any remaining data in buffer
          if (buffer) {
            const lines = buffer.split('\n');
            for (const line of lines) {
              if (line.startsWith('data: ')) {
                const data = line.substring(6);
                if (data === '[DONE]') {
                  setSending(false);
                  return;
                }
                if (data) {
                  setMessages(prev => prev.map(msg =>
                    msg.id === tempUserMsg.id
                      ? { ...msg, response: (msg.response || '') + data }
                      : msg
                  ));
                }
              }
            }
          }
          break;
        }

        buffer += decoder.decode(value, { stream: true });
        
        // SSE format: events are separated by \n\n
        // Process complete events
        let eventEndIndex;
        while ((eventEndIndex = buffer.indexOf('\n\n')) !== -1) {
          const eventText = buffer.substring(0, eventEndIndex);
          buffer = buffer.substring(eventEndIndex + 2);
          
          if (!eventText.trim()) continue;
          
          // Parse SSE event - look for data: lines
          const lines = eventText.split('\n');
          let eventType = 'token';
          let data = '';
          
          for (const line of lines) {
            if (line.startsWith('event: ')) {
              eventType = line.substring(7).trim();
            } else if (line.startsWith('data: ')) {
              // Get content after "data: " - preserve everything including spaces
              const content = line.substring(6);
              data += content; // Direct concatenation preserves all characters
            }
          }
          
          if (eventType === 'done' || data === '[DONE]') {
            setSending(false);
            return;
          }
          
          if (data && data !== '[DONE]') {
            // Update response incrementally - concatenate exactly as received
            setMessages(prev => prev.map(msg =>
              msg.id === tempUserMsg.id
                ? { ...msg, response: (msg.response || '') + data }
                : msg
            ));
          }
        }
      }

      setSending(false);
    } catch (error) {
      console.error('Failed to send message:', error);
      setMessages(prev => prev.map(msg =>
        msg.id === tempUserMsg.id
          ? { ...msg, response: msg.response || 'Sorry, I encountered an error. Please try again.' }
          : msg
      ));
      setSending(false);
    }
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      handleSend();
    }
  };

  return (
    <div className="chat-interface">
      <h2>💬 Chat</h2>

      <div className="chat-messages">
        {messages.length === 0 && !isLoading && (
          <div className="empty-state">
            <p>Start a conversation! Upload a PDF first, then ask questions about it.</p>
          </div>
        )}

        {isLoading && (
          <div className="empty-state">
            <p>Processing PDF... Please wait.</p>
          </div>
        )}

        {messages.map((msg) => (
          <div key={msg.id} className="message-group">
            <div className="message user-message">
              <div className="message-content">{msg.text}</div>
            </div>
            {(msg.response || msg.response === '') && (
              <div className="message bot-message">
                {msg.response ? (
                  <div
                    className="message-content formatted-response"
                    dangerouslySetInnerHTML={{ __html: formatMessage(msg.response) }}
                  />
                ) : (
                  <div className="message-content typing-indicator">
                    <span></span>
                    <span></span>
                    <span></span>
                  </div>
                )}
              </div>
            )}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="chat-input-container">
        <textarea
          className="chat-input"
          value={inputMessage}
          onChange={(e) => setInputMessage(e.target.value)}
          onKeyPress={handleKeyPress}
          placeholder="Type your message here... (Press Enter to send)"
          disabled={sending || !sessionId || isLoading}
          rows="3"
        />
        <button
          className="send-button"
          onClick={handleSend}
          disabled={!inputMessage.trim() || sending || !sessionId || isLoading}
        >
          {sending ? 'Sending...' : 'Send'}
        </button>
      </div>
    </div>
  );
}

export default ChatInterface;

