package com.creditscoring.dto.scoring;

import com.creditscoring.domain.ScoringRule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoringRuleResponse {

    private Long id;
    private String ruleKey;
    private String name;
    private String description;
    private Double thresholdValue;
    private Integer pointsImpact;
    private Boolean active;

    public static ScoringRuleResponse fromEntity(ScoringRule rule) {
        return new ScoringRuleResponse(
                rule.getId(),
                rule.getRuleKey(),
                rule.getName(),
                rule.getDescription(),
                rule.getThresholdValue(),
                rule.getPointsImpact(),
                rule.getActive()
        );
    }
}
