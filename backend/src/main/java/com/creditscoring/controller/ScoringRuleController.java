package com.creditscoring.controller;

import com.creditscoring.domain.ScoringRule;
import com.creditscoring.dto.scoring.ScoringRuleResponse;
import com.creditscoring.dto.scoring.ScoringRuleUpdateRequest;
import com.creditscoring.repository.ScoringRuleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// Simple CRUD over ScoringRule - lets an admin tune thresholds/points and
// turn rules on/off without touching code. Adding a brand new *type* of
// check still requires a code change in ScoringEngineService.
@RestController
@RequestMapping("/api/scoring-rules")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ScoringRuleController {

    private final ScoringRuleRepository scoringRuleRepository;

    @GetMapping
    public ResponseEntity<List<ScoringRuleResponse>> getAll() {
        List<ScoringRuleResponse> rules = scoringRuleRepository.findAll().stream()
                .map(ScoringRuleResponse::fromEntity)
                .toList();
        return ResponseEntity.ok(rules);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ScoringRuleResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody ScoringRuleUpdateRequest request
    ) {
        ScoringRule rule = scoringRuleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Scoring rule not found: " + id));

        rule.setThresholdValue(request.getThresholdValue());
        rule.setPointsImpact(request.getPointsImpact());
        rule.setActive(request.getActive());

        ScoringRule saved = scoringRuleRepository.save(rule);
        return ResponseEntity.ok(ScoringRuleResponse.fromEntity(saved));
    }
}
