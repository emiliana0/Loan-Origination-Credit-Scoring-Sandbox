package com.creditscoring.domain;

import com.creditscoring.domain.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "loan_application")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Кой е подал заявката - винаги User с роля APPLICANT
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;

    // --- Данни от формата за кандидатстване ---
    @Column(nullable = false)
    private BigDecimal monthlyIncome;

    @Column(nullable = false)
    private BigDecimal monthlyDebt;

    @Column(nullable = false)
    private Integer termMonths;

    @Column(nullable = false)
    private BigDecimal requestedAmount;

    // --- Ако решението е контра-оферта, тук се пазят новите условия ---
    private BigDecimal offeredAmount;
    private Integer offeredTermMonths;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status;

    // Ако анализатор е направил override на автоматичното решение
    private Boolean overridden;

    @Column(columnDefinition = "TEXT")
    private String overrideJustification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private User reviewedBy;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.status == null) {
            this.status = ApplicationStatus.SUBMITTED;
        }
        if (this.overridden == null) {
            this.overridden = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}
