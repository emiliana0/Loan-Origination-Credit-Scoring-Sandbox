package com.creditscoring.controller;

import com.creditscoring.dto.loan.LoanApplicationRequest;
import com.creditscoring.dto.loan.LoanApplicationResponse;
import com.creditscoring.dto.loan.LoanDecisionRequest;
import com.creditscoring.dto.scoring.ScoringResultResponse;
import com.creditscoring.security.UserPrincipal;
import com.creditscoring.service.LoanApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
public class LoanApplicationController {

    private final LoanApplicationService loanApplicationService;

    // Only APPLICANT may submit a new application
    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<LoanApplicationResponse> submit(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LoanApplicationResponse response = loanApplicationService.submit(request, principal.getUser());
        return ResponseEntity.ok(response);
    }

    // Applicant sees only their own applications
    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<List<LoanApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(loanApplicationService.getMyApplications(principal.getId()));
    }

    // Analyst/admin queue - every application in the system
    @GetMapping
    @PreAuthorize("hasAnyRole('ANALYST', 'ADMIN')")
    public ResponseEntity<List<LoanApplicationResponse>> getAllApplications() {
        return ResponseEntity.ok(loanApplicationService.getAllApplications());
    }

    // Single application detail - applicant may only view their own,
    // analyst/admin may view any (ownership check happens in the service)
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ANALYST', 'ADMIN')")
    public ResponseEntity<LoanApplicationResponse> getById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LoanApplicationResponse response = loanApplicationService.getById(
                id, principal.getId(), principal.getUser().getRole()
        );
        return ResponseEntity.ok(response);
    }

    // Analyst decision - approve / reject / counter-offer, always justified
    @PatchMapping("/{id}/decision")
    @PreAuthorize("hasRole('ANALYST')")
    public ResponseEntity<LoanApplicationResponse> decide(
            @PathVariable Long id,
            @Valid @RequestBody LoanDecisionRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LoanApplicationResponse response = loanApplicationService.decide(id, request, principal.getUser());
        return ResponseEntity.ok(response);
    }

    // Explainability breakdown for a single application's score
    @GetMapping("/{id}/score")
    @PreAuthorize("hasAnyRole('APPLICANT', 'ANALYST', 'ADMIN')")
    public ResponseEntity<ScoringResultResponse> getScore(
            @PathVariable Long id,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        ScoringResultResponse response = loanApplicationService.getScoringResult(
                id, principal.getId(), principal.getUser().getRole()
        );
        return ResponseEntity.ok(response);
    }
}
