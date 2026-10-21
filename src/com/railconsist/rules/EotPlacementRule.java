package com.railconsist.rules;

import com.railconsist.model.CarType;
import com.railconsist.model.EndOfTrainDevice;
import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.ArrayList;
import java.util.List;

/**
 * Validates FRA 49 CFR § 232.403 Rear-of-Train Telemetry Device regulations.
 * Main track trains must have an operative telemetry device (EOTD/FRED) or crewed caboose on the rear vehicle.
 */
public class EotPlacementRule implements ConsistRule {

    @Override
    public String getRuleCode() {
        return "FRA-49CFR-232.403";
    }

    @Override
    public String getRuleName() {
        return "Rear End Telemetry / Caboose Compliance";
    }

    @Override
    public List<RuleViolation> evaluate(TrainConsist consist) {
        List<RuleViolation> violations = new ArrayList<>();
        if (consist == null || consist.isEmpty()) {
            return violations;
        }

        RollingStock rearVehicle = consist.getRearVehicle();
        if (rearVehicle == null) {
            return violations;
        }

        boolean hasEot = rearVehicle.getCarType() == CarType.END_OF_TRAIN_DEVICE;
        boolean hasCaboose = rearVehicle.getCarType() == CarType.CABOOSE;

        if (!hasEot && !hasCaboose) {
            violations.add(new RuleViolation(
                    getRuleCode(),
                    getRuleName(),
                    String.format("Missing Rear Marker: Rear vehicle '%s' is a %s. Train requires an operative EOTD (FRED) or Caboose at rear.",
                            rearVehicle.getVehicleId(), rearVehicle.getCarType()),
                    SafetySeverity.VIOLATION,
                    List.of(consist.getVehicleCount() - 1),
                    "Couple an operative End-Of-Train Device (EOTD) or Caboose to the rear knuckle."
            ));
        } else if (rearVehicle instanceof EndOfTrainDevice eot) {
            if (eot.getBatteryPercent() < 20.0) {
                violations.add(new RuleViolation(
                        getRuleCode(),
                        getRuleName(),
                        String.format("Low EOTD Battery: EOTD '%s' battery is at %.0f%% (Minimum 20%% required for dispatch).",
                                eot.getVehicleId(), eot.getBatteryPercent()),
                        SafetySeverity.WARNING,
                        List.of(consist.getVehicleCount() - 1),
                        "Replace or recharge EOTD battery pack prior to dispatch."
                ));
            }
            if (!eot.isTelemetryRadioLinked()) {
                violations.add(new RuleViolation(
                        getRuleCode(),
                        getRuleName(),
                        String.format("EOTD Telemetry Offline: Head-end locomotive has lost two-way radio telemetry link with EOTD '%s'.",
                                eot.getVehicleId()),
                        SafetySeverity.CRITICAL_HALT,
                        List.of(consist.getVehicleCount() - 1),
                        "Test two-way radio link between lead locomotive cab and rear EOTD unit."
                ));
            }
        }

        return violations;
    }
}
