package com.DTO;

import com.entity.Payment;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class PaymentDTO {
    private Long id;
    private String paymentName;
    private Double totalAmount;
    private LocalDate paymentDate;
    private String status;
    private List<StudentPaymentDTO> studentPayments;

    public PaymentDTO(Long id, String paymentName, Double totalAmount, LocalDate paymentDate, String status) {
        this.id = id;
        this.paymentName = paymentName;
        this.totalAmount = totalAmount;
        this.paymentDate = paymentDate;
        this.status = status;
    }

    public static PaymentDTO fromEntity(Payment payment) {
        PaymentDTO dto = new PaymentDTO(
                payment.getId(),
                payment.getPaymentName(),
                payment.getTotalAmount(),
                payment.getPaymentDate(),
                payment.getStatus()
        );
        if (payment.getStudentPayments() != null) {
            dto.setStudentPayments(payment.getStudentPayments().stream()
                    .map(StudentPaymentDTO::fromEntity)
                    .collect(java.util.stream.Collectors.toList()));
        }
        return dto;
    }
}