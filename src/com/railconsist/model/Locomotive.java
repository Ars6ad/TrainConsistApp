package com.railconsist.model;

/**
 * High-horsepower railway locomotive power unit (e.g. GE ES44AC, EMD SD70ACe).
 */
public class Locomotive extends RollingStock {
    public enum TractionType {
        DIESEL_ELECTRIC,
        ELECTRIC_CATENARY,
        DUAL_MODE,
        HYDROGEN_FUEL_CELL,
        BATTERY_ELECTRIC
    }

    private final int horsepower;
    private final double tractiveEffortKN;
    private final TractionType tractionType;
    private double fuelLevelPercent;
    private boolean isLeadUnit;
    private final boolean dynamicBrakingEquipped;
    private final boolean positiveTrainControlEquipped;
    private final boolean distributedPowerCapable;

    public Locomotive(
            String vehicleId,
            String reportingMark,
            String roadNumber,
            double tareWeightTons,
            double lengthMeters,
            double maxSpeedKmh,
            int horsepower,
            double tractiveEffortKN,
            TractionType tractionType,
            boolean isLeadUnit
    ) {
        super(
                vehicleId,
                reportingMark,
                roadNumber,
                CarType.LOCOMOTIVE,
                tareWeightTons,
                tareWeightTons, // Locomotives carry fuel, tare is roughly equal to operating weight
                lengthMeters,
                maxSpeedKmh,
                BrakeStatus.OPERATIONAL,
                CouplerType.AAR_TYPE_F,
                "RFID-LOC-" + vehicleId,
                2022
        );
        this.horsepower = Math.max(500, horsepower);
        this.tractiveEffortKN = Math.max(100.0, tractiveEffortKN);
        this.tractionType = tractionType != null ? tractionType : TractionType.DIESEL_ELECTRIC;
        this.fuelLevelPercent = 100.0;
        this.isLeadUnit = isLeadUnit;
        this.dynamicBrakingEquipped = true;
        this.positiveTrainControlEquipped = true;
        this.distributedPowerCapable = true;
    }

    public int getHorsepower() {
        return horsepower;
    }

    public double getTractiveEffortKN() {
        return tractiveEffortKN;
    }

    public TractionType getTractionType() {
        return tractionType;
    }

    public double getFuelLevelPercent() {
        return fuelLevelPercent;
    }

    public void setFuelLevelPercent(double fuelLevelPercent) {
        this.fuelLevelPercent = Math.max(0.0, Math.min(100.0, fuelLevelPercent));
    }

    public boolean isLeadUnit() {
        return isLeadUnit;
    }

    public void setLeadUnit(boolean leadUnit) {
        isLeadUnit = leadUnit;
    }

    public boolean isDynamicBrakingEquipped() {
        return dynamicBrakingEquipped;
    }

    public boolean isPositiveTrainControlEquipped() {
        return positiveTrainControlEquipped;
    }

    public boolean isDistributedPowerCapable() {
        return distributedPowerCapable;
    }

    @Override
    public String getCargoDescription() {
        return "Locomotive Traction Unit (" + horsepower + " HP, " + tractionType + ")";
    }

    @Override
    public HazmatClass getHazmatClass() {
        return HazmatClass.NONE;
    }

    @Override
    public String toString() {
        return String.format("%s [LOCO: %d HP, Lead: %b] - %.1fT, %.1fm",
                vehicleId, horsepower, isLeadUnit, grossWeightTons, lengthMeters);
    }
}
