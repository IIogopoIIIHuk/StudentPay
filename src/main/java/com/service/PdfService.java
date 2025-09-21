package com.service;

import com.entity.Payment;
import com.entity.User;
import com.repo.PaymentRepository;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    // TODO: Implement the actual PDF generation logic here
    public Resource createGeneralPdfStatement(int month, int year) {
        // Placeholder method to resolve compiler error
        throw new UnsupportedOperationException("PDF generation not yet implemented");
    }

    // TODO: Implement the actual PDF generation logic here
    public Resource createPersonalPdfStatement(int month, int year, Long studentId) {
        // Placeholder method to resolve compiler error
        throw new UnsupportedOperationException("PDF generation not yet implemented");
    }
}