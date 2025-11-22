package com.ragdemo.service;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PdfProcessingService {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    public void processPdf(MultipartFile file) throws IOException {
        log.info("Processing PDF file: {}", file.getOriginalFilename());

        // Parse PDF document directly from input stream to avoid file system issues
        String pdfText;
        try (InputStream inputStream = file.getInputStream()) {
            byte[] pdfBytes = inputStream.readAllBytes();
            try (PDDocument pdDocument = Loader.loadPDF(pdfBytes)) {
                PDFTextStripper stripper = new PDFTextStripper();
                pdfText = stripper.getText(pdDocument);
            }
        }

        if (pdfText == null || pdfText.trim().isEmpty()) {
            throw new IOException("PDF file is empty or could not be parsed");
        }

        // Create document from extracted text
        Document document = Document.from(pdfText);

        log.info("PDF loaded successfully. Text length: {}", document.text().length());

        // Split document into chunks
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 50);
        List<TextSegment> segments = splitter.split(document);

        log.info("Document split into {} segments", segments.size());

        // Generate embeddings and store in vector database
        for (TextSegment segment : segments) {
            var embedding = embeddingModel.embed(segment).content();
            embeddingStore.add(embedding, segment);
        }

        log.info("Successfully processed and stored {} segments in vector database", segments.size());
    }
}
