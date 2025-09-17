package com.service;

import com.entity.Payment;
import com.entity.User;
import com.repo.PaymentRepository;
import com.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PdfService {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    public byte[] generatePdf(int month, int year, String type, Long studentId) throws Exception {
        // Заглушка
        return "PDF content placeholder".getBytes();
    }
}