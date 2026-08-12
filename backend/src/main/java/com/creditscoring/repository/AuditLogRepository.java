package com.creditscoring.repository;

import com.creditscoring.domain.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByLoanApplicationIdOrderByTimestampDesc(Long loanApplicationId);
}
