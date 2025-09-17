package com.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "stipends")
@NoArgsConstructor
public class Stipend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "type_name", nullable = false, unique = true)
    private String typeName;

    @Column(name = "amount", nullable = false)
    private Double amount;

    public Stipend(String typeName, Double amount) {
        this.typeName = typeName;
        this.amount = amount;
    }
}