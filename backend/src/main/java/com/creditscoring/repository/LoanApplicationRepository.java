package com.creditscoring.repository;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanApplicationRepository extends JpaRepository<LoanApplication, Long> {
    List<LoanApplication> findByApplicantId(Long applicantId);
    List<LoanApplication> findByStatus(ApplicationStatus status);
}
