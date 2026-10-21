package com.railconsist.rules;

import com.railconsist.model.TrainConsist;
import com.railconsist.physics.ConsistMetrics;
import com.railconsist.physics.TractiveEffortCalculator;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates maximum train length against siding capacity and gross tonnage against draft gear tensile ratings.
 */
public class MaxTrainLengthAndTonnageRule implements ConsistRule {

    private final double maxSidingLengthMeters;
    private final double maxGrossTonnage;

    public MaxTrainLengthAndTonnageRule(double maxSidingLengthMeters, double maxGrossTonnage) {
        this.maxSidingLengthMeters = maxSidingLengthMeters > 0 ? maxSidingLengthMeters : 3000.0; // 3 km default (~10,000 ft)
        this.maxGrossTonnage = maxGrossTonnage > 0 ? maxGrossTonnage : 18000.0; // 18,000 tons default
    }

    public MaxTrainLengthAndTonnageRule() {
        this(3000.0, 18000.0);
    }

    @Override
    public String getRuleCode() {
        return "AAR-LMT-002";
    }

    @Override
    public String getRuleName() {
        return "Route Clearance, Siding Length & Tonnage Limits";
    }

    @Override
    public List<RuleViolation> evaluate(TrainConsist consist) {
        List<RuleViolation> violations = new ArrayList<>();
        if (consist == null || consist.isEmpty()) {
            return violations;
        }

        TractiveEffortCalculator calc = new TractiveEffortCalculator();
        ConsistMetrics metrics = calc.computeMetrics(consist);

        if (metrics.totalLengthMeters() > maxSidingLengthMeters) {
            violations.add(new RuleViolation(
                    getRuleCode(),
                    getRuleName(),
                    String.format("Excessive Train Length: Consist length is %.1fm (%.0fft), exceeding maximum passing siding capacity of %.1fm.",
                            metrics.totalLengthMeters(), metrics.totalLengthFeet(), maxSidingLengthMeters),
                    SafetySeverity.WARNING,
                    List.of(),
                    "Reduce train length or file special dispatch authority for long-train meet/pass protocol."
            ));
        }

        if (metrics.grossWeightTons() > maxGrossTonnage) {
            violations.add(new RuleViolation(
                    getRuleCode(),
                    getRuleName(),
                    String.format("Excessive Tonnage: Consist gross weight is %.1f tons, exceeding route limit of %.1f tons.",
                            metrics.grossWeightTons(), maxGrossTonnage),
                    SafetySeverity.VIOLATION,
                    List.of(),
                    "Split consist or add mid-train Distributed Power Units (DPU) to distribute draft forces."
            ));
        }

        return violations;
    }
}
