package com.railconsist.rules;

import com.railconsist.model.CarType;
import com.railconsist.model.HazmatClass;
import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates FRA 49 CFR § 174.85 Hazardous Materials Marshalling Regulations.
 * Enforces buffer car separation between placarded hazmat cars and occupied power units / cabooses.
 */
public class HazmatBufferRule implements ConsistRule {

    @Override
    public String getRuleCode() {
        return "FRA-49CFR-174.85";
    }

    @Override
    public String getRuleName() {
        return "Hazardous Materials Buffer Car Separation Rule";
    }

    @Override
    public List<RuleViolation> evaluate(TrainConsist consist) {
        List<RuleViolation> violations = new ArrayList<>();
        if (consist == null || consist.isEmpty()) {
            return violations;
        }

        List<RollingStock> vehicles = consist.getVehicles();
        int totalVehicles = vehicles.size();

        // Identify indices of all locomotives and occupied units (caboose/passenger)
        List<Integer> powerUnitIndices = new ArrayList<>();
        List<Integer> occupiedUnitIndices = new ArrayList<>();

        for (int i = 0; i < totalVehicles; i++) {
            RollingStock v = vehicles.get(i);
            if (v.getCarType() == CarType.LOCOMOTIVE) {
                powerUnitIndices.add(i);
            } else if (v.getCarType() == CarType.CABOOSE || v.getCarType() == CarType.PASSENGER_COACH) {
                occupiedUnitIndices.add(i);
            }
        }

        // Validate each placarded hazmat car
        for (int i = 0; i < totalVehicles; i++) {
            RollingStock v = vehicles.get(i);
            HazmatClass hazmat = v.getHazmatClass();

            if (hazmat != null && hazmat.isHazardous()) {
                int requiredBuffer = hazmat.getRequiredBufferCars();
                // If consist is short, FRA allows reduced buffer, but at least 1 buffer car minimum
                int minBufferCars = Math.min(requiredBuffer, Math.max(1, totalVehicles / 8));

                // 1. Check distance to locomotives:
                for (int locoIndex : powerUnitIndices) {
                    int separation = Math.abs(i - locoIndex);
                    int actualBufferCars = separation - 1;

                    if (actualBufferCars < minBufferCars) {
                        violations.add(new RuleViolation(
                                getRuleCode(),
                                getRuleName(),
                                String.format("Hazmat car '%s' (%s) is at index %d, only %d car(s) from Locomotive at index %d (Required buffer: %d non-hazmat cars).",
                                        v.getVehicleId(), hazmat.getPlacardCode(), i, actualBufferCars, locoIndex, minBufferCars),
                                SafetySeverity.CRITICAL_HALT,
                                List.of(i, locoIndex),
                                String.format("Insert at least %d non-hazardous buffer car(s) between locomotive and hazmat car '%s'.",
                                        minBufferCars - actualBufferCars, v.getVehicleId())
                        ));
                    }
                }

                // 2. Check distance to occupied caboose / passenger cars:
                for (int occIndex : occupiedUnitIndices) {
                    int separation = Math.abs(i - occIndex);
                    int actualBufferCars = separation - 1;

                    if (actualBufferCars < 1) {
                        violations.add(new RuleViolation(
                                getRuleCode(),
                                getRuleName(),
                                String.format("Hazmat car '%s' (%s) is directly coupled to occupied unit '%s' at index %d.",
                                        v.getVehicleId(), hazmat.getPlacardCode(), vehicles.get(occIndex).getVehicleId(), occIndex),
                                SafetySeverity.CRITICAL_HALT,
                                List.of(i, occIndex),
                                "Provide at least 1 buffer car between hazmat car and occupied crew caboose / passenger coach."
                        ));
                    }
                }

                // 3. Check for incompatible adjacent hazmat (e.g., Explosives 1.1 coupled directly to Flammables 3):
                if (i + 1 < totalVehicles) {
                    RollingStock next = vehicles.get(i + 1);
                    if (hazmat == HazmatClass.CLASS_1_EXPLOSIVES && next.getHazmatClass() == HazmatClass.CLASS_3_FLAMMABLE_LIQUIDS) {
                        violations.add(new RuleViolation(
                                getRuleCode(),
                                "Hazmat Chemical Incompatibility",
                                String.format("Incompatible hazmat: Class 1 Explosive '%s' is directly coupled to Class 3 Flammable '%s'.",
                                        v.getVehicleId(), next.getVehicleId()),
                                SafetySeverity.CRITICAL_HALT,
                                List.of(i, i + 1),
                                "Separate explosive and flammable tank cars into distinct blocks with buffer separation."
                        ));
                    }
                }
            }
        }

        return violations;
    }
}
