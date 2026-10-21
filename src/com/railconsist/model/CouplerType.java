package com.railconsist.model;

/**
 * Standard AAR/UIC Coupler mechanisms.
 */
public enum CouplerType {
    AAR_TYPE_E("Standard AAR Type E Knuckle Coupler", 1800.0),
    AAR_TYPE_F("Interlocking Safety Type F Coupler", 2200.0),
    ROTARY_DUMP("Rotary Dump Car Coupler", 2000.0),
    TIGHTLOCK("Passenger Tightlock Coupler", 2500.0);

    private final String description;
    private final double maxTensileStrengthKN;

    CouplerType(String description, double maxTensileStrengthKN) {
        this.description = description;
        this.maxTensileStrengthKN = maxTensileStrengthKN;
    }

    public String getDescription() {
        return description;
    }

    public double getMaxTensileStrengthKN() {
        return maxTensileStrengthKN;
    }
}
