package com.creditscoring.config;

import com.creditscoring.domain.ScoringRule;
import com.creditscoring.repository.ScoringRuleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

// Seeds a starter set of scoring rules on first run, so the engine has
// something to evaluate against without manual setup.
// This will be expanded/replaced by proper seed data in a later phase.
@Component
@RequiredArgsConstructor
public class ScoringRuleSeeder implements CommandLineRunner {

    private final ScoringRuleRepository scoringRuleRepository;

    @Override
    public void run(String... args) {
        if (scoringRuleRepository.count() > 0) {
            return; // already seeded
        }

        scoringRuleRepository.saveAll(List.of(
                ScoringRule.builder()
                        .ruleKey("DTI_THRESHOLD")
                        .name("High debt-to-income ratio")
                        .description("Flags applicants whose monthly debt is too high relative to their income")
                        .thresholdValue(0.40)
                        .pointsImpact(-30)
                        .active(true)
                        .build(),
                ScoringRule.builder()
                        .ruleKey("MIN_INCOME")
                        .name("Minimum income requirement")
                        .description("Flags applicants below the minimum monthly income")
                        .thresholdValue(500.0)
                        .pointsImpact(-40)
                        .active(true)
                        .build(),
                ScoringRule.builder()
                        .ruleKey("HIGH_INCOME_BONUS")
                        .name("High income bonus")
                        .description("Rewards applicants with a comfortably high monthly income")
                        .thresholdValue(3000.0)
                        .pointsImpact(15)
                        .active(true)
                        .build(),
                ScoringRule.builder()
                        .ruleKey("AMOUNT_TO_INCOME_RATIO")
                        .name("Requested amount too high relative to income")
                        .description("Flags requests that are large relative to income over the loan term")
                        .thresholdValue(0.5)
                        .pointsImpact(-20)
                        .active(true)
                        .build()
        ));
    }
}
