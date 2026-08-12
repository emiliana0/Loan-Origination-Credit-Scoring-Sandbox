package com.creditscoring.domain.enums;

public enum ApplicationStatus {
    SUBMITTED,      // подадена от кандидата
    IN_REVIEW,      // системата/анализаторът я оценява
    APPROVED,       // одобрена
    REJECTED,       // отказана
    COUNTER_OFFER   // предложени различни условия
}
