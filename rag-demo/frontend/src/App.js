import React, { useState, useEffect } from 'react';
import './App.css';
import FileUpload from './components/FileUpload';
import ChatInterface from './components/ChatInterface';
import axios from 'axios';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

function App() {
  const [sessionId, setSessionId] = useState(null);
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    createNewSession();
  }, []);

  const createNewSession = async () => {
    try {
      const response = await axios.post(`${API_BASE_URL}/chat/session`);
      setSessionId(response.data.sessionId);
    } catch (error) {
      console.error('Failed to create session:', error);
    }
  };

  return (
    <div className="App">
      <div className="container">
        <header className="header">
          <h1>🤖 RAG Demo Chatbot</h1>
          <p>Upload a PDF and chat with AI powered by Deepseek R1</p>
        </header>

        <div className="main-content">
          <div className="upload-section">
            <FileUpload 
              onUploadStart={() => setIsLoading(true)}
              onUploadComplete={() => setIsLoading(false)}
            />
          </div>

          <div className="chat-section">
            <ChatInterface 
              sessionId={sessionId}
              isLoading={isLoading}
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default App;

