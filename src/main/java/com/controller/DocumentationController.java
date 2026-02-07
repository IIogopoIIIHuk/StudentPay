package com.controller;

import com.DTO.DocumentationResponseDTO;
import com.DTO.StudentDataDTO;
import com.service.PaymentService;
import com.service.PdfService;
import lombok.Data;
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

    private final PaymentService paymentService;

    @PostMapping("/report")
    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT', 'ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<DocumentationResponseDTO> getReport(@RequestBody ReportRequest req) {
        return ResponseEntity.ok(paymentService.getDocumentationData(req.getMonth(), req.getYear(), req.getStudentId()));
    }

    @Data
    public static class ReportRequest {
        private Integer month;
        private Integer year;
        private Long studentId;
    }

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