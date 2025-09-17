package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "stipend_settings")
@NoArgsConstructor
public class StipendSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "profkom_deduction_percent")
    private Double profkomDeductionPercent;

    @Column(name = "brsm_deduction_percent")
    private Double brsmDeductionPercent;

    public StipendSettings(Double profkomDeductionPercent, Double brsmDeductionPercent) {
        this.profkomDeductionPercent = profkomDeductionPercent;
        this.brsmDeductionPercent = brsmDeductionPercent;
    }
}