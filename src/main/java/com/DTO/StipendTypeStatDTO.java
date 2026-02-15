package com.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StipendTypeStatDTO {
    private String stipendType;
    private Long studentCount;
    private Double percentage;
}