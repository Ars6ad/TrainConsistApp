package com.railconsist.rules;

import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates FRA 49 CFR Part 232 Operative Brake Percentage Regulations.
 * Requires 100% operative brakes at initial terminal departure, absolute minimum 85% en route.
 */
public class BrakingPercentageRule implements ConsistRule {

    @Override
    public String getRuleCode() {
        return "FRA-49CFR-232";
    }

    @Override
    public String getRuleName() {
        return "Operative Brake Percentage Compliance";
    }

    @Override
    public List<RuleViolation> evaluate(TrainConsist consist) {
        List<RuleViolation> violations = new ArrayList<>();
        if (consist == null || consist.isEmpty()) {
            return violations;
        }

        List<RollingStock> vehicles = consist.getVehicles();
        int total = vehicles.size();
        int operativeCount = 0;
        List<Integer> defectiveIndices = new ArrayList<>();

        for (int i = 0; i < total; i++) {
            RollingStock v = vehicles.get(i);
            if (v.isOperativeBrake()) {
                operativeCount++;
            } else {
                defectiveIndices.add(i);
            }
        }

        double percentage = (total > 0) ? ((double) operativeCount / total) * 100.0 : 0.0;

        if (percentage < 85.0) {
            violations.add(new RuleViolation(
                    getRuleCode(),
                    getRuleName(),
                    String.format("Critical Brake Failure: Only %.1f%% of cars have operative brakes (%d/%d). Absolute legal minimum is 85.0%%.",
                            percentage, operativeCount, total),
                    SafetySeverity.CRITICAL_HALT,
                    defectiveIndices,
                    "Inspect and repair cutout/defective brake valves or set out defective cars before train dispatch."
            ));
        } else if (percentage < 100.0) {
            violations.add(new RuleViolation(
                    getRuleCode(),
                    getRuleName(),
                    String.format("Terminal Brake Advisory: Consist has %.1f%% operative brakes (%d/%d). FRA Class I brake test requires 100%% at initial terminal.",
                            percentage, operativeCount, total),
                    SafetySeverity.WARNING,
                    defectiveIndices,
                    "Verify if train is departing initial terminal (requires 100%) or en route interchange."
            ));
        }

        return violations;
    }
}
