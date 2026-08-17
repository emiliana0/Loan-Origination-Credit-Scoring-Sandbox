package com.creditscoring.dto.loan;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@AllArgsConstructor
public class LoanApplicationResponse {

    private Long id;
    private String applicantEmail;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyDebt;
    private Integer termMonths;
    private BigDecimal requestedAmount;
    private ApplicationStatus status;
    private Instant createdAt;

    public static LoanApplicationResponse fromEntity(LoanApplication app) {
        return new LoanApplicationResponse(
                app.getId(),
                app.getApplicant().getEmail(),
                app.getMonthlyIncome(),
                app.getMonthlyDebt(),
                app.getTermMonths(),
                app.getRequestedAmount(),
                app.getStatus(),
                app.getCreatedAt()
        );
    }
}
