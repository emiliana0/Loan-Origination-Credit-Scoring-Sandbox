package com.creditscoring.dto.scoring;

import com.creditscoring.domain.ScoringResultDetail;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ScoringDetailResponse {

    private String ruleKey;
    private String ruleName;
    private Boolean triggered;
    private Integer pointsContribution;
    private String explanation;

    public static ScoringDetailResponse fromEntity(ScoringResultDetail detail) {
        return new ScoringDetailResponse(
                detail.getRuleKey(),
                detail.getRuleName(),
                detail.getTriggered(),
                detail.getPointsContribution(),
                detail.getExplanation()
        );
    }
}
