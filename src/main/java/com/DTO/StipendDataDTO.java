package com.DTO;

import com.entity.Stipend;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
public class StipendDataDTO {
    private Long id;
    private String typeName;
    private Double amount;

    public StipendDataDTO(Long id, String typeName, Double amount) {
        this.id = id;
        this.typeName = typeName;
        this.amount = amount;
    }

    public static StipendDataDTO fromEntity(Stipend stipend) {
        return new StipendDataDTO(stipend.getId(), stipend.getTypeName(), stipend.getAmount());
    }

    public static List<StipendDataDTO> fromEntityList(List<Stipend> stipends) {
        return stipends.stream()
                .map(StipendDataDTO::fromEntity)
                .collect(Collectors.toList());
    }
}