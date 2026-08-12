package com.creditscoring.repository;

import com.creditscoring.domain.ScoringResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ScoringResultRepository extends JpaRepository<ScoringResult, Long> {
    Optional<ScoringResult> findByLoanApplicationId(Long loanApplicationId);
}
