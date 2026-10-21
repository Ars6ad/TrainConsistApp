package com.railconsist.model;

import java.util.Objects;

/**
 * Standard freight railcar carrying bulk or containerized cargo.
 */
public class FreightCar extends RollingStock {
    private String cargoCommodity;
    private HazmatClass hazmatClass;
    private String unNumber;
    private final double maxCargoCapacityTons;
    private String destinationYard;

    public FreightCar(
            String vehicleId,
            String reportingMark,
            String roadNumber,
            CarType carType,
            double tareWeightTons,
            double maxCargoCapacityTons,
            double currentCargoWeightTons,
            double lengthMeters,
            double maxSpeedKmh,
            String cargoCommodity,
            HazmatClass hazmatClass,
            String unNumber,
            String destinationYard
    ) {
        super(
                vehicleId,
                reportingMark,
                roadNumber,
                carType,
                tareWeightTons,
                tareWeightTons + Math.max(0.0, Math.min(maxCargoCapacityTons, currentCargoWeightTons)),
                lengthMeters,
                maxSpeedKmh,
                BrakeStatus.OPERATIONAL,
                CouplerType.AAR_TYPE_E,
                "AEI-FRT-" + vehicleId,
                2018
        );
        this.maxCargoCapacityTons = Math.max(10.0, maxCargoCapacityTons);
        this.cargoCommodity = cargoCommodity != null && !cargoCommodity.isBlank() ? cargoCommodity : "Empty";
        this.hazmatClass = hazmatClass != null ? hazmatClass : HazmatClass.NONE;
        this.unNumber = unNumber != null && !unNumber.isBlank() ? unNumber : null;
        this.destinationYard = destinationYard != null ? destinationYard : "Central Terminal";
    }

    public double getMaxCargoCapacityTons() {
        return maxCargoCapacityTons;
    }

    public void loadCargo(String commodity, double weightTons, HazmatClass hazmat, String unCode) {
        this.cargoCommodity = commodity;
        this.hazmatClass = hazmat != null ? hazmat : HazmatClass.NONE;
        this.unNumber = unCode;
        double cappedCargo = Math.min(maxCargoCapacityTons, Math.max(0.0, weightTons));
        this.grossWeightTons = this.tareWeightTons + cappedCargo;
    }

    public void unloadCargo() {
        this.cargoCommodity = "Empty";
        this.hazmatClass = HazmatClass.NONE;
        this.unNumber = null;
        this.grossWeightTons = this.tareWeightTons;
    }

    public boolean isLoaded() {
        return getCargoWeightTons() > 0.5;
    }

    public boolean isPlacarded() {
        return hazmatClass != null && hazmatClass.isHazardous();
    }

    public String getUnNumber() {
        return unNumber;
    }

    public String getDestinationYard() {
        return destinationYard;
    }

    public void setDestinationYard(String destinationYard) {
        this.destinationYard = Objects.requireNonNull(destinationYard, "destinationYard must not be null");
    }

    @Override
    public String getCargoDescription() {
        if (!isLoaded()) {
            return "EMPTY";
        }
        StringBuilder sb = new StringBuilder(cargoCommodity);
        if (isPlacarded()) {
            sb.append(" [HAZMAT: ").append(hazmatClass.getPlacardCode());
            if (unNumber != null) {
                sb.append(" ").append(unNumber);
            }
            sb.append("]");
        }
        return sb.toString();
    }

    @Override
    public HazmatClass getHazmatClass() {
        return hazmatClass;
    }

    @Override
    public String toString() {
        return String.format("%s [%s] Cargo: %s, %.1f/%.1fT (Gross: %.1fT)",
                vehicleId, carType, getCargoDescription(), getCargoWeightTons(), maxCargoCapacityTons, grossWeightTons);
    }
}
