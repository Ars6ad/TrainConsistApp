package com.railconsist.physics;

/**
 * Immutable engineering metrics snapshot for a train consist.
 */
public record ConsistMetrics(
        int totalVehicles,
        int locomotiveCount,
        int freightCarCount,
        int passengerCount,
        double grossWeightTons,
        double trailingWeightTons,
        double totalLengthMeters,
        double totalLengthFeet,
        int totalHorsepower,
        double horsepowerPerTonRatio,
        double operativeBrakePercentage,
        double maxAllowableSpeedKmh,
        int hazmatCarCount,
        double estimatedDrawbarPullKN
) {
    public boolean isMeetsBrakeRegulations() {
        return operativeBrakePercentage >= 85.0; // FRA standard 85% rule
    }

    public boolean isAdequatePowerForGrade(double gradePercent) {
        // High mountain grades (>1.5%) typically require HPT >= 1.8
        if (gradePercent >= 1.5) {
            return horsepowerPerTonRatio >= 1.8;
        } else if (gradePercent >= 0.8) {
            return horsepowerPerTonRatio >= 1.2;
        }
        return horsepowerPerTonRatio >= 0.8;
    }
}
