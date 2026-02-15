package com.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AnalyticsResponseDTO {
    private List<StipendTypeStatDTO> statistics;
    private Integer totalStudentsWithStipend;
}

