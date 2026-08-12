package com.creditscoring.repository;

import com.creditscoring.domain.ScoringRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ScoringRuleRepository extends JpaRepository<ScoringRule, Long> {
    List<ScoringRule> findByActiveTrue();
}
