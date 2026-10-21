package com.railconsist.physics;

import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes weight distribution across train blocks and detects string-lining / derailment hazards.
 * String-lining occurs when a lightweight empty car is placed ahead of a long block of heavy cars on sharp curves.
 */
public class WeightDistributionAnalyzer {

    public record WeightAnomaly(
            int vehicleIndex,
            String vehicleId,
            double carWeightTons,
            double trailingBlockWeightTons,
            String hazardDescription,
            double lateralToVerticalRatio
    ) {}

    public record DistributionReport(
            double headWeightTons,      // First 25% of consist
            double midWeightTons,       // Middle 50% of consist
            double tailWeightTons,      // Rear 25% of consist
            double centerOfGravityPositionMeters,
            boolean isForwardWeighted,
            List<WeightAnomaly> detectedAnomalies,
            double overallCurvingRiskScore // 0.0 (safe) to 1.0 (critical danger)
    ) {}

    /**
     * Analyzes weight distribution and detects string-lining derailment risks.
     */
    public DistributionReport analyze(TrainConsist consist) {
        if (consist == null || consist.isEmpty()) {
            return new DistributionReport(0, 0, 0, 0, true, List.of(), 0.0);
        }

        List<RollingStock> vehicles = consist.getVehicles();
        int count = vehicles.size();

        double totalWeight = 0.0;
        double weightedPositionSum = 0.0;
        double currentPositionMeters = 0.0;

        int quarterCount = Math.max(1, count / 4);
        double headWeight = 0.0;
        double midWeight = 0.0;
        double tailWeight = 0.0;

        for (int i = 0; i < count; i++) {
            RollingStock v = vehicles.get(i);
            double w = v.getGrossWeightTons();
            totalWeight += w;

            double midCarPos = currentPositionMeters + (v.getLengthMeters() / 2.0);
            weightedPositionSum += (w * midCarPos);
            currentPositionMeters += v.getLengthMeters();

            if (i < quarterCount) {
                headWeight += w;
            } else if (i >= count - quarterCount) {
                tailWeight += w;
            } else {
                midWeight += w;
            }
        }

        double cogMeters = totalWeight > 0.0 ? (weightedPositionSum / totalWeight) : 0.0;
        boolean forwardWeighted = headWeight >= tailWeight;

        // Anomaly & String-lining detection:
        List<WeightAnomaly> anomalies = new ArrayList<>();
        double highestRiskScore = 0.0;

        for (int i = 0; i < count - 1; i++) {
            RollingStock curr = vehicles.get(i);
            // If current car is light (< 35 tons)
            if (curr.getGrossWeightTons() < 35.0 && !curr.getCarType().isLocomotive()) {
                // Calculate total trailing tonnage behind this car
                double trailingTonnage = 0.0;
                for (int j = i + 1; j < count; j++) {
                    trailingTonnage += vehicles.get(j).getGrossWeightTons();
                }

                // If trailing tonnage exceeds 4,000 tons behind an empty car, severe string-lining risk exists on curves:
                if (trailingTonnage > 3500.0) {
                    // Estimated L/V (Lateral to Vertical force ratio)
                    // High draft pull on curve generates inward lateral force
                    double draftForceKN = trailingTonnage * 0.15;
                    double verticalWheelForceKN = (curr.getGrossWeightTons() / 4.0) * 9.81; // per wheel approx
                    double lvRatio = verticalWheelForceKN > 0.0 ? Math.min(1.5, (draftForceKN * 0.04) / verticalWheelForceKN) : 0.0;

                    double risk = Math.min(1.0, trailingTonnage / 7000.0);
                    highestRiskScore = Math.max(highestRiskScore, risk);

                    anomalies.add(new WeightAnomaly(
                            i,
                            curr.getVehicleId(),
                            round(curr.getGrossWeightTons(), 1),
                            round(trailingTonnage, 1),
                            String.format("High String-Lining Derailment Risk: Light car (%.1f T) with %.1f T trailing. Heavy draft forces on curves may cause wheel lift.",
                                    curr.getGrossWeightTons(), trailingTonnage),
                            round(lvRatio, 3)
                    ));
                }
            }
        }

        return new DistributionReport(
                round(headWeight, 1),
                round(midWeight, 1),
                round(tailWeight, 1),
                round(cogMeters, 1),
                forwardWeighted,
                anomalies,
                round(highestRiskScore, 2)
        );
    }

    private static double round(double val, int places) {
        double factor = Math.pow(10, places);
        return Math.round(val * factor) / factor;
    }
}
