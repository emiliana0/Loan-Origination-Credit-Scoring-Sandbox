package com.creditscoring.dto.loan;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class LoanApplicationRequest {

    @NotNull(message = "Месечният доход е задължителен")
    @Positive(message = "Месечният доход трябва да е положително число")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Месечните задължения са задължителни")
    @PositiveOrZero(message = "Месечните задължения не могат да са отрицателни")
    private BigDecimal monthlyDebt;

    @NotNull(message = "Срокът е задължителен")
    @Min(value = 3, message = "Минималният срок е 3 месеца")
    @Max(value = 120, message = "Максималният срок е 120 месеца")
    private Integer termMonths;

    @NotNull(message = "Исканата сума е задължителна")
    @Positive(message = "Исканата сума трябва да е положително число")
    private BigDecimal requestedAmount;
}
