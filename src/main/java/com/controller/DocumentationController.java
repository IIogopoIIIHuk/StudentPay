package com.controller;

import com.DTO.StudentDataDTO;
import com.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/documentation")
public class DocumentationController {

    private final PdfService pdfService;

    @GetMapping("/download/general")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<Resource> downloadGeneralPaymentStatement(@RequestParam int month, @RequestParam int year) {
        Resource file = pdfService.createGeneralPdfStatement(month, year);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"general_statement.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }

    @GetMapping("/download/personal")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<Resource> downloadPersonalPaymentStatement(@RequestParam int month, @RequestParam int year, @RequestParam Long studentId) {
        Resource file = pdfService.createPersonalPdfStatement(month, year, studentId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"personal_statement.pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(file);
    }
}