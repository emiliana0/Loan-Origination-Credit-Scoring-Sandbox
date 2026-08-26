package com.creditscoring.dto.loan;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.ScoringResult;
import com.creditscoring.domain.enums.ApplicationStatus;
import com.creditscoring.domain.enums.Decision;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class LoanApplicationResponse {

    private Long id;
    private String applicantEmail;
    private String applicantFullName;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyDebt;
    private Integer termMonths;
    private BigDecimal requestedAmount;
    private BigDecimal offeredAmount;
    private Integer offeredTermMonths;
    private ApplicationStatus status;
    private Boolean overridden;
    private String overrideJustification;
    private Integer totalScore;
    private Decision decision;
    private Instant createdAt;

    public static LoanApplicationResponse fromEntity(LoanApplication app) {
        return fromEntity(app, null);
    }

    // scoringResult may be null (e.g. right before evaluation happens)
    public static LoanApplicationResponse fromEntity(LoanApplication app, ScoringResult scoringResult) {
        return new LoanApplicationResponse(
                app.getId(),
                app.getApplicant().getEmail(),
                app.getApplicant().getFullName(),
                app.getMonthlyIncome(),
                app.getMonthlyDebt(),
                app.getTermMonths(),
                app.getRequestedAmount(),
                app.getOfferedAmount(),
                app.getOfferedTermMonths(),
                app.getStatus(),
                app.getOverridden(),
                app.getOverrideJustification(),
                scoringResult != null ? scoringResult.getTotalScore() : null,
                scoringResult != null ? scoringResult.getDecision() : null,
                app.getCreatedAt()
        );
    }
}
