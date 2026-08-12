package com.creditscoring.domain;

import jakarta.persistence.*;
import lombok.*;

// Един ред = едно правило, което е било проверено при оценката,
// и дали е сработило + с колко точки е допринесло.
// Това е "explainability" частта - анализаторът вижда точно
// защо системата е взела дадено решение.
@Entity
@Table(name = "scoring_result_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringResultDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "scoring_result_id", nullable = false)
    private ScoringResult scoringResult;

    @Column(nullable = false)
    private String ruleKey;

    @Column(nullable = false)
    private String ruleName;

    @Column(nullable = false)
    private Boolean triggered;

    @Column(nullable = false)
    private Integer pointsContribution;

    @Column(columnDefinition = "TEXT")
    private String explanation;
}
