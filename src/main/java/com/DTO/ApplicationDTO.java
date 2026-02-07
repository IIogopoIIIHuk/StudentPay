package com.DTO;

import com.entity.Application;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class ApplicationDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Integer monthOfRequest;
    private Integer yearOfRequest;
    private String status;
    private String documentUrl;

    public static ApplicationDTO fromEntity(Application application) {
        ApplicationDTO dto = new ApplicationDTO();
        dto.setId(application.getId());
        dto.setStudentId(application.getStudent().getId());
        dto.setStudentName(application.getStudent().getName());
        dto.setMonthOfRequest(application.getMonthOfRequest());
        dto.setYearOfRequest(application.getYearOfRequest());
        dto.setStatus(application.getStatus());

        if (application.getPdfDocument() != null && application.getPdfDocument().length > 0) {
            String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/applications/download/")
                    .path(application.getId().toString())
                    .toUriString();

            dto.setDocumentUrl(fullUrl);
        }
        return dto;
    }
}