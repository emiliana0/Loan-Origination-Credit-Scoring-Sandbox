package com.creditscoring.controller;

import com.creditscoring.dto.loan.LoanApplicationRequest;
import com.creditscoring.dto.loan.LoanApplicationResponse;
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

    // Only an APPLICANT can submit a new application
    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<LoanApplicationResponse> submit(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LoanApplicationResponse response = loanApplicationService.submit(request, principal.getUser());
        return ResponseEntity.ok(response);
    }

    // Applicants can only see their own applications
    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<List<LoanApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(loanApplicationService.getMyApplications(principal.getId()));
    }

    // Both roles allowed here - ownership check happens inside the service:
    // an APPLICANT may only view the score for their own application.
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

