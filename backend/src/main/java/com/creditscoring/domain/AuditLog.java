package com.creditscoring.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// Един ред = една промяна по заявка. Пази се кой, кога, какво поле,
// каква беше старата стойност и каква е новата.
// В Фаза 7 ще закачим това автоматично през @EntityListeners,
// засега само модела на данните.
@Entity
@Table(name = "audit_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performed_by_id")
    private User performedBy;

    @Column(nullable = false)
    private String action;          // напр. "STATUS_CHANGE", "OVERRIDE", "CREATED"

    private String fieldChanged;
    private String oldValue;
    private String newValue;

    @Column(nullable = false, updatable = false)
    private Instant timestamp;

    @PrePersist
    protected void onCreate() {
        this.timestamp = Instant.now();
    }
}
