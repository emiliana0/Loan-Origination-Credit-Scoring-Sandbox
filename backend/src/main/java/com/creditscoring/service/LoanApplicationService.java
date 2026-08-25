package com.creditscoring.service;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.ScoringResult;
import com.creditscoring.domain.User;
import com.creditscoring.domain.enums.ApplicationStatus;
import com.creditscoring.domain.enums.Role;
import com.creditscoring.dto.loan.LoanApplicationRequest;
import com.creditscoring.dto.loan.LoanApplicationResponse;
import com.creditscoring.dto.scoring.ScoringResultResponse;
import com.creditscoring.exception.AccessDeniedCustomException;
import com.creditscoring.repository.LoanApplicationRepository;
import com.creditscoring.repository.ScoringResultRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LoanApplicationService {

    private final LoanApplicationRepository loanApplicationRepository;
    private final ScoringResultRepository scoringResultRepository;
    private final ScoringEngineService scoringEngineService;

    public LoanApplicationResponse submit(LoanApplicationRequest request, User applicant) {
        LoanApplication application = LoanApplication.builder()
                .applicant(applicant)
                .monthlyIncome(request.getMonthlyIncome())
                .monthlyDebt(request.getMonthlyDebt())
                .termMonths(request.getTermMonths())
                .requestedAmount(request.getRequestedAmount())
                .build();

        LoanApplication saved = loanApplicationRepository.save(application);

        // Run the scoring engine right away so the analyst has a result
        // to review as soon as the application shows up in their queue.
        scoringEngineService.evaluate(saved);

        saved.setStatus(ApplicationStatus.IN_REVIEW);
        saved = loanApplicationRepository.save(saved);

        return LoanApplicationResponse.fromEntity(saved);
    }

    public List<LoanApplicationResponse> getMyApplications(Long applicantId) {
        return loanApplicationRepository.findByApplicantId(applicantId).stream()
                .map(LoanApplicationResponse::fromEntity)
                .toList();
    }

    public ScoringResultResponse getScoringResult(Long applicationId, Long requestingUserId, Role requestingRole) {
        LoanApplication application = loanApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("Loan application not found: " + applicationId));

        // Applicants may only view the score for their own application.
        // Analysts and admins may view any application's score.
        if (requestingRole == Role.APPLICANT && !application.getApplicant().getId().equals(requestingUserId)) {
            throw new AccessDeniedCustomException("You may only view the score for your own applications");
        }

        ScoringResult result = scoringResultRepository.findByLoanApplicationId(applicationId)
                .orElseThrow(() -> new EntityNotFoundException("No scoring result yet for application: " + applicationId));

        return ScoringResultResponse.fromEntity(result);
    }
}


