package com.creditscoring.controller;

import com.creditscoring.dto.loan.LoanApplicationRequest;
import com.creditscoring.dto.loan.LoanApplicationResponse;
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

    // Само APPLICANT може да подава заявки
    @PostMapping
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<LoanApplicationResponse> submit(
            @Valid @RequestBody LoanApplicationRequest request,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        LoanApplicationResponse response = loanApplicationService.submit(request, principal.getUser());
        return ResponseEntity.ok(response);
    }

    // Кандидатът вижда само собствените си заявки
    @GetMapping("/my")
    @PreAuthorize("hasRole('APPLICANT')")
    public ResponseEntity<List<LoanApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        return ResponseEntity.ok(loanApplicationService.getMyApplications(principal.getId()));
    }
}
