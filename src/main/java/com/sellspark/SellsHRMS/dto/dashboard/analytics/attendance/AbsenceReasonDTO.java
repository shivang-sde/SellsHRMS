package com.sellspark.SellsHRMS.dto.dashboard.analytics.attendance;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * DTO for absence reasons distribution
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AbsenceReasonDTO {

    private String reason;
    private Long count;
    private BigDecimal percentage;

    /**
     * Constructor for JPQL projection
     */
    public AbsenceReasonDTO(String reason, Long count) {
        this.reason = reason;
        this.count = count;
    }

    /**
     * Calculate percentage after fetching all results
     */
    public void calculatePercentage(Long totalCount) {
        if (totalCount == null || totalCount == 0) {
            this.percentage = BigDecimal.ZERO;
        } else {
            this.percentage = BigDecimal.valueOf(count)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalCount), 2, RoundingMode.HALF_UP);
        }
    }
}
