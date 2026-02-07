package com.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DocumentationResponseDTO {
    private LocalDate paymentDate;
    private Double totalAmount;
    private List<StudentSummaryDTO> students;
    private StudentInfoDTO student;
    private CalculationDetailsDTO calculations;

    @Data
    @AllArgsConstructor
    public static class StudentSummaryDTO {
        private Long id;
        private String fullName;
        private Double scholarshipAmount;
    }

    @Data
    @AllArgsConstructor
    public static class StudentInfoDTO {
        private Long id;
        private String fullName;
        private String group;
    }

    @Data
    @Builder
    public static class CalculationDetailsDTO {
        private Double baseScholarship;
        private Double bonus;
        private Double profcomPercent;
        private Double brsmPercentage;
        private Double profcomDeduction;
        private Double brsmDeduction;
        private Double totalAmount;
    }
}