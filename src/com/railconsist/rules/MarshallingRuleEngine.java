package com.railconsist.rules;

import com.railconsist.model.TrainConsist;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Enterprise safety and marshalling rule compliance evaluation engine.
 */
public class MarshallingRuleEngine {

    private final List<ConsistRule> activeRules;

    public MarshallingRuleEngine() {
        this.activeRules = new ArrayList<>();
        registerDefaultRules();
    }

    private void registerDefaultRules() {
        activeRules.add(new HazmatBufferRule());
        activeRules.add(new BrakingPercentageRule());
        activeRules.add(new StringLiningPreventionRule());
        activeRules.add(new EotPlacementRule());
        activeRules.add(new MaxTrainLengthAndTonnageRule());
    }

    public synchronized void registerRule(ConsistRule rule) {
        if (rule != null && !activeRules.contains(rule)) {
            activeRules.add(rule);
        }
    }

    public synchronized List<ConsistRule> getActiveRules() {
        return Collections.unmodifiableList(new ArrayList<>(activeRules));
    }

    /**
     * Executes all registered rules against the given train consist and returns an audit report.
     */
    public SafetyAuditReport auditConsist(TrainConsist consist) {
        if (consist == null || consist.isEmpty()) {
            return new SafetyAuditReport(
                    consist != null ? consist.getConsistId() : "N/A",
                    Instant.now(),
                    false,
                    0,
                    1,
                    0,
                    1,
                    0.0,
                    List.of(new RuleViolation("EMPTY-CONSIST", "Consist Population", "Consist contains no vehicles", SafetySeverity.CRITICAL_HALT, List.of(), "Add locomotives and rolling stock."))
            );
        }

        List<RuleViolation> allViolations = new ArrayList<>();
        int criticalHalts = 0;
        int violationsCount = 0;
        int warningsCount = 0;

        for (ConsistRule rule : activeRules) {
            List<RuleViolation> findings = rule.evaluate(consist);
            for (RuleViolation f : findings) {
                allViolations.add(f);
                switch (f.severity()) {
                    case CRITICAL_HALT -> criticalHalts++;
                    case VIOLATION -> violationsCount++;
                    case WARNING -> warningsCount++;
                    default -> {}
                }
            }
        }

        boolean dispatchReady = (criticalHalts == 0) && (violationsCount == 0);

        // Calculate score: 100 minus penalty for findings
        double score = 100.0 - (criticalHalts * 30.0) - (violationsCount * 15.0) - (warningsCount * 5.0);
        score = Math.max(0.0, Math.min(100.0, score));

        return new SafetyAuditReport(
                consist.getConsistId(),
                Instant.now(),
                dispatchReady,
                activeRules.size(),
                violationsCount,
                warningsCount,
                criticalHalts,
                Math.round(score * 10.0) / 10.0,
                allViolations
        );
    }
}
