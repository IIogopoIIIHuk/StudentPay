package com.service;

import com.DTO.ApplicationDTO;
import com.entity.Application;
import com.repo.ApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    @Transactional
    public ApplicationDTO createApplication(Long studentId, Integer month, Integer year) {
        Application application = new Application();
        application.setStudent(userService.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found")));
        application.setMonthOfRequest(month);
        application.setYearOfRequest(year);
        Application savedApplication = applicationRepository.save(application);
        return ApplicationDTO.fromEntity(savedApplication);
    }

    public List<ApplicationDTO> getAllApplications() {
        return applicationRepository.findAll().stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public List<ApplicationDTO> getStudentApplications(Long studentId) {
        return applicationRepository.findByStudentId(studentId).stream()
                .map(ApplicationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public Optional<ApplicationDTO> updateApplicationStatus(Long id, String status) {
        return applicationRepository.findById(id).map(application -> {
            application.setStatus(status);
            return ApplicationDTO.fromEntity(applicationRepository.save(application));
        });
    }

    @Transactional
    public Optional<ApplicationDTO> attachPdfToApplication(Long id, byte[] pdfBytes) {
        return applicationRepository.findById(id).map(application -> {
            application.setPdfDocument(pdfBytes);
            application.setStatus("закрыт");
            return ApplicationDTO.fromEntity(applicationRepository.save(application));
        });
    }
}