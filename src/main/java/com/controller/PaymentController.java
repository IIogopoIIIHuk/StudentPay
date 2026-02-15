package com.controller;

import com.DTO.PaymentDTO;
import com.entity.User;
import com.exception.AppError;
import com.repo.UserRepository;
import com.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final UserRepository userRepository;

    @PostMapping("/calculate")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<?> calculateMonthlyPayment() {
        try {
            PaymentDTO payment = paymentService.calculateAndCreateMonthlyPayment();
            return ResponseEntity.ok(payment);
        } catch (IllegalStateException e) {
            return new ResponseEntity<>(new AppError(HttpStatus.BAD_REQUEST.value(), e.getMessage()), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при расчете: " + e.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/can-calculate")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<Map<String, Boolean>> checkCalculationStatus() {
        boolean canCalculate = paymentService.canCreatePayment();
        return ResponseEntity.ok(Map.of("canCalculate", canCalculate));
    }


    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT', 'ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<List<PaymentDTO>> getAllPayments(@RequestParam(required = false) Integer month, @RequestParam(required = false) Integer year) {
        if (month != null && year != null) {
            return ResponseEntity.ok(paymentService.getPaymentsByDate(month, year));
        }
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT', 'ROLE_DEAN_EMPLOYEE')")
    public ResponseEntity<?> getPaymentDetails(@PathVariable Long id) {
        Optional<PaymentDTO> payment = paymentService.getPaymentDetails(id);
        if (payment.isEmpty()) {
            return new ResponseEntity<>(new AppError(HttpStatus.NOT_FOUND.value(), "Ведомость с ID " + id + " не найдена"), HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(payment.get());
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<PaymentDTO> updatePaymentStatus(@PathVariable Long id, @RequestParam String status) {
        return paymentService.updatePaymentStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<Void> deletePayment(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<PaymentDTO>> getMyPayments(
            @RequestParam(required = false) Integer month,
            @RequestParam(required = false) Integer year
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userRepository.findByUsername(username);

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        if (month != null && year != null) {
            return ResponseEntity.ok(paymentService.getMyPaymentsByDate(currentUser.get().getId(), month, year));
        }

        return ResponseEntity.ok(paymentService.getAllMyPayments(currentUser.get().getId()));
    }
}