package com.ragdemo.controller;

import com.ragdemo.service.PdfProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class FileUploadController {

    private final PdfProcessingService pdfProcessingService;

    @PostMapping("/pdf")
    public ResponseEntity<Map<String, String>> uploadPdf(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            if (file.isEmpty()) {
                response.put("error", "File is empty");
                return ResponseEntity.badRequest().body(response);
            }

            if (!"application/pdf".equals(file.getContentType())) {
                response.put("error", "Only PDF files are allowed");
                return ResponseEntity.badRequest().body(response);
            }

            pdfProcessingService.processPdf(file);
            response.put("message", "PDF processed successfully");
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error processing PDF", e);
            response.put("error", "Failed to process PDF: " + e.getMessage());
            return ResponseEntity.internalServerError().body(response);
        }
    }
}
