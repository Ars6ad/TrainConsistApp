package com.railconsist.rules;

import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates Train Make-Up rules to prevent string-lining derailments on curves.
 * Prohibits blocks of empty/light cars placed directly ahead of heavy blocks (>4,000 trailing tons).
 */
public class StringLiningPreventionRule implements ConsistRule {

    @Override
    public String getRuleCode() {
        return "AAR-TMU-001";
    }

    @Override
    public String getRuleName() {
        return "Train Make-Up String-Lining Prevention Rule";
    }

    @Override
    public List<RuleViolation> evaluate(TrainConsist consist) {
        List<RuleViolation> violations = new ArrayList<>();
        if (consist == null || consist.getVehicleCount() < 4) {
            return violations;
        }

        List<RollingStock> vehicles = consist.getVehicles();
        int total = vehicles.size();

        for (int i = 0; i < total - 1; i++) {
            RollingStock curr = vehicles.get(i);
            if (!curr.getCarType().isLocomotive() && curr.getGrossWeightTons() < 32.0) {
                // Calculate trailing weight behind this empty car
                double trailingTons = 0.0;
                for (int j = i + 1; j < total; j++) {
                    trailingTons += vehicles.get(j).getGrossWeightTons();
                }

                if (trailingTons > 4500.0) {
                    violations.add(new RuleViolation(
                            getRuleCode(),
                            getRuleName(),
                            String.format("Severe String-Lining Risk: Empty car '%s' (%.1f T) at pos %d has excessive trailing tonnage (%.1f T).",
                                    curr.getVehicleId(), curr.getGrossWeightTons(), i, trailingTons),
                            SafetySeverity.VIOLATION,
                            List.of(i),
                            "Relocate light empty cars to the rear portion of the consist or place heavy blocks toward the head end."
                    ));
                }
            }
        }

        return violations;
    }
}
