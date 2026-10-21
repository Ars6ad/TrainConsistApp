package com.railconsist.rules;

import java.time.Instant;
import java.util.List;

/**
 * Complete audit report produced by the Marshalling Rule Engine.
 */
public record SafetyAuditReport(
        String consistId,
        Instant auditTimestamp,
        boolean isDispatchReady,
        int totalRulesEvaluated,
        int totalViolationsCount,
        int totalWarningsCount,
        int criticalHaltsCount,
        double complianceScorePercent, // 0.0 to 100.0%
        List<RuleViolation> violations
) {
    public boolean hasCriticalHalts() {
        return criticalHaltsCount > 0;
    }

    public boolean hasViolations() {
        return totalViolationsCount > 0;
    }
}
