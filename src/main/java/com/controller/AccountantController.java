package com.controller;

import com.DTO.ApplicationDTO;
import com.DTO.PaymentDTO;
import com.service.ApplicationService;
import com.service.PaymentService;
import com.service.PdfService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/accountant")
@PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
public class AccountantController {

    private final PaymentService paymentService;
    private final ApplicationService applicationService;
    private final PdfService pdfService;


    // Эндпоинт для кнопки "Расчет"
    @PostMapping("/calculate-payment")
    public ResponseEntity<?> calculateMonthlyPayment() {
        try {
            PaymentDTO payment = paymentService.calculateAndCreateMonthlyPayment();
            return ResponseEntity.ok(payment);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        List<PaymentDTO> payments = paymentService.getAllPayments();
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/payments/{id}")
    public ResponseEntity<PaymentDTO> getPaymentDetails(@PathVariable Long id) {
        return paymentService.getPaymentDetails(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/payments/{id}/status")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        return paymentService.updatePaymentStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/payments/{id}")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/applications/{id}/status")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(@PathVariable Long id, @RequestParam String status) {
        return applicationService.updateApplicationStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/applications/{id}/attach-pdf")
    public ResponseEntity<ApplicationDTO> attachPdfToApplication(@PathVariable Long id, @RequestBody byte[] pdfBytes) {
        return applicationService.attachPdfToApplication(id, pdfBytes)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documentation/download")
    public ResponseEntity<byte[]> downloadPdf(@RequestParam int month,
                                              @RequestParam int year,
                                              @RequestParam String type,
                                              @RequestParam(required = false) Long studentId) {
        try {
            byte[] pdfBytes = pdfService.generatePdf(month, year, type, studentId);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "statement.pdf");
            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}