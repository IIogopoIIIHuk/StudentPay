package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "student_details")
@NoArgsConstructor
public class StudentDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "gpa")
    private Double gpa = 0.0;

    @Column(name = "absences_hours")
    private Integer absencesHours = 0;

    @Column(name = "has_retakes")
    private Boolean hasRetakes = false;

    @Column(name = "bonus_amount")
    private Double bonusAmount = 0.0;

    @Column(name = "stipend_type")
    private String stipendType;

    @Column(name = "has_stipend")
    private Boolean hasStipend = false;

    public StudentDetails(User user) {
        this.user = user;
    }
}