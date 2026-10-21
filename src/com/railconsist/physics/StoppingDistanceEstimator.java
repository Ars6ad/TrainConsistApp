package com.railconsist.physics;

import com.railconsist.model.TrainConsist;

/**
 * Estimates emergency braking and full service stopping distances
 * based on speed, tonnage, operative brake percentage, and track grade.
 */
public class StoppingDistanceEstimator {

    private static final double GRAVITY = 9.80665; // m/s^2

    public record StoppingResult(
            double initialSpeedKmh,
            double emergencyStoppingDistanceMeters,
            double serviceStoppingDistanceMeters,
            double emergencyStopDurationSeconds,
            double decelerationRateMpss,
            boolean withinSafeSignalingBlock
    ) {}

    /**
     * Calculates stopping distances for a consist under emergency and service braking.
     *
     * @param consist The train consist
     * @param initialSpeedKmh Initial speed when brakes are applied
     * @param gradePercent Gradient (+ for uphill, - for downhill)
     * @return StoppingResult with distances and durations
     */
    public StoppingResult calculateStoppingDistance(TrainConsist consist, double initialSpeedKmh, double gradePercent) {
        if (consist == null || consist.isEmpty() || initialSpeedKmh <= 0.0) {
            return new StoppingResult(0, 0, 0, 0, 0, true);
        }

        TractiveEffortCalculator calc = new TractiveEffortCalculator();
        ConsistMetrics metrics = calc.computeMetrics(consist);

        // Convert speed km/h to m/s
        double speedMs = initialSpeedKmh / 3.6;

        // Base brake friction coefficient (tread brakes / composite brake shoes)
        // Friction decreases slightly at higher speeds:
        double nominalFriction = 0.28 / (1.0 + 0.005 * initialSpeedKmh);

        // Operative brake ratio factor (0.0 to 1.0)
        double brakeFactor = Math.min(1.0, metrics.operativeBrakePercentage() / 100.0);

        // Net retarding braking force ratio of train weight
        double netBrakingRatio = 0.12 * brakeFactor * nominalFriction;

        // Grade effect on acceleration (a_grade = g * sin(theta) ≈ g * gradePercent / 100)
        double gradeDecelerationEffect = GRAVITY * (gradePercent / 100.0);

        // Net deceleration (m/s^2) in emergency
        double emergencyDecel = Math.max(0.15, (netBrakingRatio * GRAVITY) + gradeDecelerationEffect);

        // Service deceleration is roughly 60% of emergency deceleration
        double serviceDecel = emergencyDecel * 0.60;

        // Brake propagation delay (air brake signal travels down brake pipe at ~280 m/s):
        double brakePipePropagationTime = Math.max(1.0, metrics.totalLengthMeters() / 280.0);

        // Emergency stopping distance: d = v * t_delay + v^2 / (2 * a)
        double emergencyDistance = (speedMs * brakePipePropagationTime) + (Math.pow(speedMs, 2) / (2.0 * emergencyDecel));
        double serviceDistance = (speedMs * (brakePipePropagationTime * 1.5)) + (Math.pow(speedMs, 2) / (2.0 * serviceDecel));

        double stopDurationSeconds = brakePipePropagationTime + (speedMs / emergencyDecel);

        // Standard 2-mile (3200 meter) signaling block safety check
        boolean withinBlock = emergencyDistance <= 2500.0;

        return new StoppingResult(
                round(initialSpeedKmh, 1),
                round(emergencyDistance, 1),
                round(serviceDistance, 1),
                round(stopDurationSeconds, 1),
                round(emergencyDecel, 3),
                withinBlock
        );
    }

    private static double round(double val, int places) {
        double factor = Math.pow(10, places);
        return Math.round(val * factor) / factor;
    }
}
