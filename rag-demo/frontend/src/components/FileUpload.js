import React, { useState } from 'react';
import axios from 'axios';
import './FileUpload.css';

const API_BASE_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080/api';

function FileUpload({ onUploadStart, onUploadComplete }) {
  const [file, setFile] = useState(null);
  const [uploading, setUploading] = useState(false);
  const [message, setMessage] = useState('');
  const [messageType, setMessageType] = useState('');

  const handleFileChange = (e) => {
    const selectedFile = e.target.files[0];
    if (selectedFile && selectedFile.type === 'application/pdf') {
      setFile(selectedFile);
      setMessage('');
    } else {
      setMessage('Please select a PDF file');
      setMessageType('error');
      setFile(null);
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setMessage('Please select a file first');
      setMessageType('error');
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    setUploading(true);
    setMessage('');
    onUploadStart();

    try {
      const response = await axios.post(`${API_BASE_URL}/upload/pdf`, formData, {
        headers: {
          'Content-Type': 'multipart/form-data',
        },
      });

      setMessage(response.data.message || 'PDF uploaded and processed successfully!');
      setMessageType('success');
      setFile(null);
      document.getElementById('file-input').value = '';
    } catch (error) {
      setMessage(error.response?.data?.error || 'Failed to upload PDF');
      setMessageType('error');
    } finally {
      setUploading(false);
      onUploadComplete();
    }
  };

  return (
    <div className="file-upload">
      <h2>📄 Upload PDF</h2>
      <div className="upload-area">
        <input
          id="file-input"
          type="file"
          accept="application/pdf"
          onChange={handleFileChange}
          disabled={uploading}
          className="file-input"
        />
        <label htmlFor="file-input" className="file-label">
          {file ? file.name : 'Choose PDF File'}
        </label>
      </div>
      
      {file && (
        <button 
          onClick={handleUpload} 
          disabled={uploading}
          className="upload-button"
        >
          {uploading ? 'Processing...' : 'Upload & Process'}
        </button>
      )}

      {message && (
        <div className={`message ${messageType}`}>
          {message}
        </div>
      )}
    </div>
  );
}

export default FileUpload;

