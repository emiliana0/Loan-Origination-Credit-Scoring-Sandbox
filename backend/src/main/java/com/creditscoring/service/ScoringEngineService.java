package com.creditscoring.service;

import com.creditscoring.domain.LoanApplication;
import com.creditscoring.domain.ScoringResult;
import com.creditscoring.domain.ScoringResultDetail;
import com.creditscoring.domain.ScoringRule;
import com.creditscoring.domain.enums.Decision;
import com.creditscoring.repository.ScoringResultRepository;
import com.creditscoring.repository.ScoringRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

// Rule-based scoring engine.
// Rules themselves are stored in the database (ScoringRule) so thresholds and
// points can be tuned without touching this code. Each rule's "ruleKey" maps
// to one specific check implemented below - adding a brand new *type* of check
// still requires a code change here, but adjusting existing thresholds/points
// only requires editing the ScoringRule row.
@Service
@RequiredArgsConstructor
public class ScoringEngineService {

    private static final int BASE_SCORE = 100;
    private static final int APPROVE_THRESHOLD = 70;
    private static final int MANUAL_REVIEW_THRESHOLD = 50;

    private final ScoringRuleRepository scoringRuleRepository;
    private final ScoringResultRepository scoringResultRepository;

    public ScoringResult evaluate(LoanApplication application) {
        List<ScoringRule> activeRules = scoringRuleRepository.findByActiveTrue();

        ScoringResult result = ScoringResult.builder()
                .loanApplication(application)
                .build();

        int totalScore = BASE_SCORE;

        for (ScoringRule rule : activeRules) {
            RuleCheckResult checkResult = evaluateRule(rule, application);

            if (checkResult.triggered()) {
                totalScore += rule.getPointsImpact();
            }

            ScoringResultDetail detail = ScoringResultDetail.builder()
                    .ruleKey(rule.getRuleKey())
                    .ruleName(rule.getName())
                    .triggered(checkResult.triggered())
                    .pointsContribution(checkResult.triggered() ? rule.getPointsImpact() : 0)
                    .explanation(checkResult.explanation())
                    .build();

            result.addDetail(detail);
        }

        // keep the score within a sane 0-100 range
        totalScore = Math.max(0, Math.min(100, totalScore));

        result.setTotalScore(totalScore);
        result.setDecision(decideFromScore(totalScore));

        return scoringResultRepository.save(result);
    }

    private Decision decideFromScore(int score) {
        if (score >= APPROVE_THRESHOLD) {
            return Decision.APPROVE;
        }
        if (score >= MANUAL_REVIEW_THRESHOLD) {
            return Decision.MANUAL_REVIEW;
        }
        return Decision.REJECT;
    }

    // Dispatches to the correct check based on ruleKey.
    // New rule *types* are added here; new rule *instances* (different
    // thresholds/points for an existing type) only need a new ScoringRule row.
    private RuleCheckResult evaluateRule(ScoringRule rule, LoanApplication application) {
        return switch (rule.getRuleKey()) {
            case "DTI_THRESHOLD" -> checkDtiThreshold(rule, application);
            case "MIN_INCOME" -> checkMinIncome(rule, application);
            case "HIGH_INCOME_BONUS" -> checkHighIncomeBonus(rule, application);
            case "AMOUNT_TO_INCOME_RATIO" -> checkAmountToIncomeRatio(rule, application);
            default -> new RuleCheckResult(false, "Unknown rule key, skipped: " + rule.getRuleKey());
        };
    }

    private RuleCheckResult checkDtiThreshold(ScoringRule rule, LoanApplication app) {
        if (app.getMonthlyIncome().compareTo(BigDecimal.ZERO) == 0) {
            return new RuleCheckResult(true, "Monthly income is zero - DTI cannot be computed, treated as high risk");
        }

        BigDecimal dti = app.getMonthlyDebt()
                .divide(app.getMonthlyIncome(), 4, RoundingMode.HALF_UP);

        boolean triggered = dti.doubleValue() > rule.getThresholdValue();
        String explanation = String.format(
                "DTI = %.2f (threshold %.2f)", dti.doubleValue(), rule.getThresholdValue()
        );
        return new RuleCheckResult(triggered, explanation);
    }

    private RuleCheckResult checkMinIncome(ScoringRule rule, LoanApplication app) {
        boolean triggered = app.getMonthlyIncome().doubleValue() < rule.getThresholdValue();
        String explanation = String.format(
                "Monthly income %.2f (minimum required %.2f)",
                app.getMonthlyIncome().doubleValue(), rule.getThresholdValue()
        );
        return new RuleCheckResult(triggered, explanation);
    }

    private RuleCheckResult checkHighIncomeBonus(ScoringRule rule, LoanApplication app) {
        boolean triggered = app.getMonthlyIncome().doubleValue() > rule.getThresholdValue();
        String explanation = String.format(
                "Monthly income %.2f (bonus threshold %.2f)",
                app.getMonthlyIncome().doubleValue(), rule.getThresholdValue()
        );
        return new RuleCheckResult(triggered, explanation);
    }

    private RuleCheckResult checkAmountToIncomeRatio(ScoringRule rule, LoanApplication app) {
        BigDecimal totalIncomeOverTerm = app.getMonthlyIncome()
                .multiply(BigDecimal.valueOf(app.getTermMonths()));

        if (totalIncomeOverTerm.compareTo(BigDecimal.ZERO) == 0) {
            return new RuleCheckResult(true, "Total income over term is zero, treated as high risk");
        }

        BigDecimal ratio = app.getRequestedAmount()
                .divide(totalIncomeOverTerm, 4, RoundingMode.HALF_UP);

        boolean triggered = ratio.doubleValue() > rule.getThresholdValue();
        String explanation = String.format(
                "Requested amount / total income over term = %.2f (threshold %.2f)",
                ratio.doubleValue(), rule.getThresholdValue()
        );
        return new RuleCheckResult(triggered, explanation);
    }

    private record RuleCheckResult(boolean triggered, String explanation) {
    }
}
