package com.controller;

import com.DTO.AnalyticsResponseDTO;
import com.exception.AppError;
import com.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analytics")
public class AnalyticsController {

    private final StudentService studentService;

    @GetMapping("/stipends")
    @PreAuthorize("hasAnyRole('ROLE_DEAN_EMPLOYEE', 'ROLE_ACCOUNTANT')")
    public ResponseEntity<?> getStipendStats() {
        try {
            AnalyticsResponseDTO stats = studentService.getStipendAnalytics();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return new ResponseEntity<>(new AppError(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ошибка при формировании аналитики"), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}