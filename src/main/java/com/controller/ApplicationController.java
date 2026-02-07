package com.controller;

import com.DTO.ApplicationDTO;
import com.DTO.CreateApplicationDTO;
import com.entity.User;
import com.repo.ApplicationRepository;
import com.repo.UserRepository;
import com.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<ApplicationDTO> createApplication(@RequestBody CreateApplicationDTO createApplicationDTO) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userRepository.findByUsername(username);

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        ApplicationDTO newApplication = applicationService.createApplication(currentUser.get().getId(), createApplicationDTO);
        return ResponseEntity.ok(newApplication);
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<ApplicationDTO>> getMyApplications() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        Optional<User> currentUser = userRepository.findByUsername(username);

        if (currentUser.isEmpty()) {
            return ResponseEntity.status(401).build();
        }

        List<ApplicationDTO> applications = applicationService.getApplicationsByStudentId(currentUser.get().getId());
        return ResponseEntity.ok(applications);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<List<ApplicationDTO>> getAllApplications() {
        List<ApplicationDTO> applications = applicationService.getAllApplications();
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<ApplicationDTO> updateApplicationStatus(@PathVariable Long id, @RequestParam String status) {
        return applicationService.updateApplicationStatus(id, status)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/upload")
    @PreAuthorize("hasRole('ROLE_ACCOUNTANT')")
    public ResponseEntity<ApplicationDTO> uploadDocument(@PathVariable Long id, @RequestParam("file") MultipartFile file) throws IOException {
        return applicationService.attachPdfToApplication(id, file.getBytes())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/download")
    @PreAuthorize("hasAnyRole('ROLE_ACCOUNTANT', 'ROLE_USER')")
    public ResponseEntity<byte[]> downloadDocument(@PathVariable Long id) {
        return applicationRepository.findById(id)
                .map(app -> ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"doc.pdf\"")
                        .contentType(MediaType.APPLICATION_PDF)
                        .body(app.getPdfDocument()))
                .orElse(ResponseEntity.notFound().build());
    }
}