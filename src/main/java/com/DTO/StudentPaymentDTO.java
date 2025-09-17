package com.DTO;

import com.entity.StudentPayment;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StudentPaymentDTO {
    private Long id;
    private Long studentId;
    private String studentName;
    private Double amount;

    public static StudentPaymentDTO fromEntity(StudentPayment studentPayment) {
        StudentPaymentDTO dto = new StudentPaymentDTO();
        dto.setId(studentPayment.getId());
        dto.setStudentId(studentPayment.getStudent().getId());
        dto.setStudentName(studentPayment.getStudent().getName());
        dto.setAmount(studentPayment.getAmount());
        return dto;
    }
}