package com.controller;

import com.DTO.ApplicationDTO;
import com.DTO.PaymentDTO;
import com.service.ApplicationService;
import com.service.PaymentService;
import com.repo.UserRepository;
import com.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/student")
@PreAuthorize("hasRole('ROLE_USER')")
public class StudentController {

    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final ApplicationService applicationService;

    @GetMapping("/payments")
    public ResponseEntity<List<PaymentDTO>> getMyPayments() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<PaymentDTO> payments = paymentService.getAllPayments().stream()
                .filter(paymentDTO -> paymentDTO.getStudentPayments() != null &&
                        paymentDTO.getStudentPayments().stream().anyMatch(sp -> sp.getStudentId().equals(currentUser.getId())))
                .collect(Collectors.toList());

        return ResponseEntity.ok(payments);
    }

    @PostMapping("/applications")
    public ResponseEntity<ApplicationDTO> createApplication(@RequestBody Map<String, Integer> request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Integer month = request.get("month");
        Integer year = request.get("year");

        ApplicationDTO newApplication = applicationService.createApplication(currentUser.getId(), month, year);
        return ResponseEntity.ok(newApplication);
    }

    @GetMapping("/applications")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<ApplicationDTO> applications = applicationService.getStudentApplications(currentUser.getId());
        return ResponseEntity.ok(applications);
    }
}