package com.DTO;

import com.entity.StipendSettings;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class StipendSettingsDTO {
    private Double profkomDeductionPercent;
    private Double brsmDeductionPercent;

    public static StipendSettingsDTO fromEntity(StipendSettings settings) {
        StipendSettingsDTO dto = new StipendSettingsDTO();
        dto.setProfkomDeductionPercent(settings.getProfkomDeductionPercent());
        dto.setBrsmDeductionPercent(settings.getBrsmDeductionPercent());
        return dto;
    }
}