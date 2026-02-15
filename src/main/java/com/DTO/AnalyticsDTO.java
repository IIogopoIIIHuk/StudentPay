package com.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class AnalyticsDTO {
    private List<StipendStat> statistics;
    private long totalStudents;

    @Data
    @AllArgsConstructor
    public static class StipendStat {
        private String type;
        private long count;
        private double percentage;
    }
}