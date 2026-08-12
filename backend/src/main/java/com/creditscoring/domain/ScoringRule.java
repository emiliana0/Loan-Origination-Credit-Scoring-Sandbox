package com.creditscoring.domain;

import jakarta.persistence.*;
import lombok.*;

// Всяко правило проверява едно конкретно условие (напр. DTI над праг)
// и добавя/изважда точки от общия скор, ако условието е изпълнено.
// ruleKey свързва правилото с конкретна проверка, имплементирана в кода
// на scoring engine-а (напр. "DTI_THRESHOLD", "MIN_INCOME") - конфигурира се
// прагът/точките тук, без да пипаш кода на engine-а за всяка промяна.
@Entity
@Table(name = "scoring_rule")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScoringRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String ruleKey;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Прагова стойност, ползвана от логиката (напр. DTI праг = 0.40)
    private Double thresholdValue;

    // Колко точки добавя/изважда, ако правилото сработи (може да е отрицателно)
    @Column(nullable = false)
    private Integer pointsImpact;

    @Column(nullable = false)
    private Boolean active;

    @PrePersist
    protected void onCreate() {
        if (this.active == null) {
            this.active = true;
        }
    }
}
