package com.railconsist.rules;

public enum SafetySeverity {
    INFO("Informational Advisory"),
    WARNING("Operational Warning - Dispatch with Caution"),
    VIOLATION("Regulatory Violation - Immediate Remediation Required"),
    CRITICAL_HALT("Critical Safety Halt - Train Dispatch Prohibited");

    private final String description;

    SafetySeverity(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
