package com.creditscoring.service;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.User;
import com.creditscoring.dto.loan.LoanApplicationRequest;
import com.creditscoring.dto.loan.LoanApplicationResponse;
import com.creditscoring.repository.LoanApplicationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;

    public LoanApplicationResponse submit(LoanApplicationRequest request, User applicant) {
        LoanApplication application = LoanApplication.builder()
                .applicant(applicant)
                .monthlyIncome(request.getMonthlyIncome())
                .monthlyDebt(request.getMonthlyDebt())
                .termMonths(request.getTermMonths())
                .requestedAmount(request.getRequestedAmount())
                .build();

        LoanApplication saved = loanApplicationRepository.save(application);
        return LoanApplicationResponse.fromEntity(saved);
    }

    public List<LoanApplicationResponse> getMyApplications(Long applicantId) {
        return loanApplicationRepository.findByApplicantId(applicantId).stream()
                .map(LoanApplicationResponse::fromEntity)
                .toList();
    }
}
