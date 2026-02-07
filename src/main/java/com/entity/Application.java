package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Data
@Entity
@Table(name = "applications")
@NoArgsConstructor
public class Application {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(name = "month_of_request")
    private Integer monthOfRequest;

    @Column(name = "year_of_request")
    private Integer yearOfRequest;

    @Column(name = "status")
    private String status = "Ожидает ответа"; // Дефолтный статус

    @Lob
    @Column(name = "pdf_document")
    private byte[] pdfDocument;
}