package com.creditscoring.dto.scoring;

import com.creditscoring.domain.ScoringResult;
import com.creditscoring.domain.enums.Decision;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.Instant;
import java.util.List;

@Getter
@AllArgsConstructor
public class ScoringResultResponse {

    private Long loanApplicationId;
    private Integer totalScore;
    private Decision decision;
    private Instant evaluatedAt;
    private List<ScoringDetailResponse> details;

    public static ScoringResultResponse fromEntity(ScoringResult result) {
        List<ScoringDetailResponse> details = result.getDetails().stream()
                .map(ScoringDetailResponse::fromEntity)
                .toList();

        return new ScoringResultResponse(
                result.getLoanApplication().getId(),
                result.getTotalScore(),
                result.getDecision(),
                result.getEvaluatedAt(),
                details
        );
    }
}
