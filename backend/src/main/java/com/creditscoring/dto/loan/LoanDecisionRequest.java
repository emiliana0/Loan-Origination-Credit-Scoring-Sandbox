package com.creditscoring.dto.loan;

import com.creditscoring.domain.enums.ApplicationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanDecisionRequest {

    // Only APPROVED, REJECTED or COUNTER_OFFER are valid final decisions here
    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    // Always required - every analyst decision must be justified, per the
    // business requirements, regardless of whether it agrees with the
    // automatic scoring result or not.
    @NotBlank(message = "Justification is required")
    private String justification;

    // Only used when status = COUNTER_OFFER
    private BigDecimal offeredAmount;
    private Integer offeredTermMonths;
}
