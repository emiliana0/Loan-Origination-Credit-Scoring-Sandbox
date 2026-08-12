package com.creditscoring.domain;

import com.creditscoring.domain.enums.Decision;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

// Едно ScoringResult отговаря на едно LoanApplication (1:1).
// Съдържа финалния скор/решение, а списъкът с ScoringResultDetail
// пази explainability - кое правило какво е допринесло.
@Entity
@Table(name = "scoring_result")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_application_id", nullable = false, unique = true)
    private LoanApplication loanApplication;

    @Column(nullable = false)
    private Integer totalScore;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Decision decision;

    @Column(nullable = false, updatable = false)
    private Instant evaluatedAt;

    // cascade = ALL - когато пазим ScoringResult, детайлите се пазят автоматично с него
    // orphanRemoval - ако премахнем детайл от списъка, той се трие и от базата
    @OneToMany(mappedBy = "scoringResult", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ScoringResultDetail> details = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.evaluatedAt = Instant.now();
    }

    // helper метод - добавя детайл и слага обратната връзка автоматично
    public void addDetail(ScoringResultDetail detail) {
        details.add(detail);
        detail.setScoringResult(this);
    }
}
