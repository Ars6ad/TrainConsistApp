package com.railconsist.rules;

import java.util.List;

/**
 * Encapsulates a safety rule compliance violation with regulatory remediation actions.
 */
public record RuleViolation(
        String ruleCode,
        String ruleName,
        String message,
        SafetySeverity severity,
        List<Integer> affectedCarIndices,
        String remediationRecommendation
) {
    public boolean isBlockingDispatch() {
        return severity == SafetySeverity.VIOLATION || severity == SafetySeverity.CRITICAL_HALT;
    }
}
