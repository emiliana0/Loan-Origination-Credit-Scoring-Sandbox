package com.creditscoring.domain;

import com.creditscoring.domain.enums.Role;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

// Единен User модел за трите роли - APPLICANT, ANALYST, ADMIN.
// Ролята решава какво вижда/може да прави потребителят (виж Role enum),
// не е нужен отделен клас Applicant/Analyst - ролята е достатъчна.
@Entity
@Table(name = "app_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
    }
}
