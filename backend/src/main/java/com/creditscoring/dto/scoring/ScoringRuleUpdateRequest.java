package com.creditscoring.dto.scoring;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScoringRuleUpdateRequest {

    @NotNull
    private Double thresholdValue;

    @NotNull
    private Integer pointsImpact;

    @NotNull
    private Boolean active;
}
