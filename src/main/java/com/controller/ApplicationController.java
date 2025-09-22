package com.controller;

import com.DTO.ApplicationDTO;
import com.DTO.CreateApplicationDTO;
import com.entity.User;
import com.repo.UserRepository;
import com.service.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;
    private final UserRepository userRepository;

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
}