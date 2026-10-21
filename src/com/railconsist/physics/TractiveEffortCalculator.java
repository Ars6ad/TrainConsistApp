package com.railconsist.physics;

import com.railconsist.model.Locomotive;
import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.List;

/**
 * Calculates tractive effort, ruling grade tonnage limits, and Davis Equation train resistance.
 */
public class TractiveEffortCalculator {

    // Standard Davis equation coefficients for North American freight equipment:
    // R = A + B*v + C*v^2 (lbs or N)
    private static final double DAVIS_A = 1.3;     // Journal bearing resistance (lbs/ton)
    private static final double DAVIS_B = 0.03;    // Flange friction resistance (lbs/ton/mph)
    private static final double DAVIS_C = 0.0024;  // Aerodynamic drag coefficient
    private static final double GRAVITY_ACCEL = 9.80665; // m/s^2

    /**
     * Computes comprehensive consist metrics for physics and operational planning.
     */
    public ConsistMetrics computeMetrics(TrainConsist consist) {
        if (consist == null || consist.isEmpty()) {
            return new ConsistMetrics(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }

        List<RollingStock> vehicles = consist.getVehicles();
        int totalVehicles = vehicles.size();
        int locoCount = 0;
        int freightCount = 0;
        int passengerCount = 0;
        double grossWeight = 0.0;
        double locoWeight = 0.0;
        double totalLength = 0.0;
        int totalHorsepower = 0;
        double totalOperativeBrakingWeight = 0.0;
        double minSpeed = Double.MAX_VALUE;
        int hazmatCount = 0;
        double totalTractiveEffortKN = 0.0;

        for (RollingStock v : vehicles) {
            grossWeight += v.getGrossWeightTons();
            totalLength += v.getLengthMeters();
            minSpeed = Math.min(minSpeed, v.getMaxSpeedKmh());

            if (v.isOperativeBrake()) {
                totalOperativeBrakingWeight += v.getEffectiveBrakingWeightTons();
            }

            if (v.getHazmatClass() != null && v.getHazmatClass().isHazardous()) {
                hazmatCount++;
            }

            if (v instanceof Locomotive loco) {
                locoCount++;
                locoWeight += loco.getGrossWeightTons();
                totalHorsepower += loco.getHorsepower();
                totalTractiveEffortKN += loco.getTractiveEffortKN();
            } else if (v.getCarType().isFreight()) {
                freightCount++;
            } else if (v.getCarType() == com.railconsist.model.CarType.PASSENGER_COACH) {
                passengerCount++;
            }
        }

        double trailingWeight = Math.max(0.0, grossWeight - locoWeight);
        double hpt = grossWeight > 0.0 ? ((double) totalHorsepower / grossWeight) : 0.0;
        double brakePct = grossWeight > 0.0 ? (totalOperativeBrakingWeight / grossWeight) * 100.0 : 0.0;
        double totalLengthFeet = totalLength * 3.28084;
        double maxAllowableSpeed = (minSpeed == Double.MAX_VALUE) ? 0.0 : minSpeed;

        // Estimated drawbar pull at 1.0% ruling grade
        double drawbarPullKN = calculateTotalResistanceKN(trailingWeight, 1.0, 0.0, 50.0);

        return new ConsistMetrics(
                totalVehicles,
                locoCount,
                freightCount,
                passengerCount,
                round(grossWeight, 1),
                round(trailingWeight, 1),
                round(totalLength, 1),
                round(totalLengthFeet, 1),
                totalHorsepower,
                round(hpt, 2),
                round(brakePct, 1),
                round(maxAllowableSpeed, 1),
                hazmatCount,
                round(drawbarPullKN, 1)
        );
    }

    /**
     * Calculates total resistance force in kiloNewtons using Modified Davis Formula.
     *
     * @param weightTons Train tonnage
     * @param gradePercent Track gradient (e.g., 1.5 for 1.5% uphill)
     * @param degreeCurvature Track curvature in degrees (e.g., 2.0)
     * @param speedKmh Train speed in km/h
     * @return Total train resistance in kN
     */
    public double calculateTotalResistanceKN(double weightTons, double gradePercent, double degreeCurvature, double speedKmh) {
        if (weightTons <= 0.0) return 0.0;

        double speedMph = speedKmh * 0.621371;

        // Base Davis Rolling Resistance (lbs)
        double davisResistanceLbs = (DAVIS_A + DAVIS_B * speedMph) * weightTons + DAVIS_C * Math.pow(speedMph, 2);

        // Grade Resistance: 20 lbs per ton per 1% grade
        double gradeResistanceLbs = 20.0 * gradePercent * weightTons;

        // Curve Resistance: 0.8 lbs per ton per degree of curvature
        double curveResistanceLbs = 0.8 * degreeCurvature * weightTons;

        double totalResistanceLbs = davisResistanceLbs + gradeResistanceLbs + curveResistanceLbs;

        // Convert lbs-force to kiloNewtons (1 lbf = 0.00444822 kN)
        return totalResistanceLbs * 0.00444822;
    }

    /**
     * Calculates maximum trailing tonnage a locomotive consist can haul up a ruling grade without stalling.
     */
    public double calculateMaxTrailingTonnageOnGrade(TrainConsist consist, double gradePercent, double designSpeedKmh) {
        double totalAvailableTractiveEffortKN = 0.0;
        double locoWeightTons = 0.0;

        for (Locomotive loco : consist.getLocomotives()) {
            totalAvailableTractiveEffortKN += loco.getTractiveEffortKN();
            locoWeightTons += loco.getGrossWeightTons();
        }

        if (totalAvailableTractiveEffortKN <= 0.0) {
            return 0.0;
        }

        // Resistance per trailing ton on grade (in kN/ton)
        double unitResistanceKN = calculateTotalResistanceKN(1.0, gradePercent, 1.0, designSpeedKmh);
        double locoResistanceKN = calculateTotalResistanceKN(locoWeightTons, gradePercent, 1.0, designSpeedKmh);

        double netTractiveEffortForTrainKN = totalAvailableTractiveEffortKN - locoResistanceKN;
        if (netTractiveEffortForTrainKN <= 0.0 || unitResistanceKN <= 0.0) {
            return 0.0;
        }

        return round(netTractiveEffortForTrainKN / unitResistanceKN, 1);
    }

    private static double round(double value, int places) {
        double factor = Math.pow(10, places);
        return Math.round(value * factor) / factor;
    }
}
