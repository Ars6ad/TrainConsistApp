package com.railconsist.model;

/**
 * Operational status of rolling stock air brake components.
 */
public enum BrakeStatus {
    OPERATIONAL("Fully Operational", 1.0),
    REDUCED_EFFICIENCY("Reduced Efficiency", 0.75),
    CUT_OUT("Cut Out (Non-Operative)", 0.0),
    DEFECTIVE("Mechanically Defective", 0.0);

    private final String description;
    private final double efficiencyFactor;

    BrakeStatus(String description, double efficiencyFactor) {
        this.description = description;
        this.efficiencyFactor = efficiencyFactor;
    }

    public String getDescription() {
        return description;
    }

    public double getEfficiencyFactor() {
        return efficiencyFactor;
    }

    public boolean isOperative() {
        return efficiencyFactor > 0.0;
    }
}
