package com.railconsist.model;

import java.util.Objects;

/**
 * Abstract base class representing any rail vehicle in a train consist.
 */
public abstract class RollingStock {
    protected final String vehicleId;
    protected final String reportingMark;
    protected final String roadNumber;
    protected final CarType carType;
    protected final double tareWeightTons;      // Empty unladen weight
    protected double grossWeightTons;          // Loaded total weight
    protected final double lengthMeters;        // Coupler face to coupler face
    protected final double maxSpeedKmh;         // Maximum speed mechanical rating
    protected BrakeStatus brakeStatus;
    protected CouplerType couplerType;
    protected final String rfidTag;
    protected final int builtYear;
    protected boolean inService;

    protected RollingStock(
            String vehicleId,
            String reportingMark,
            String roadNumber,
            CarType carType,
            double tareWeightTons,
            double grossWeightTons,
            double lengthMeters,
            double maxSpeedKmh,
            BrakeStatus brakeStatus,
            CouplerType couplerType,
            String rfidTag,
            int builtYear
    ) {
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId must not be null");
        this.reportingMark = Objects.requireNonNull(reportingMark, "reportingMark must not be null");
        this.roadNumber = Objects.requireNonNull(roadNumber, "roadNumber must not be null");
        this.carType = Objects.requireNonNull(carType, "carType must not be null");
        this.tareWeightTons = Math.max(1.0, tareWeightTons);
        this.grossWeightTons = Math.max(this.tareWeightTons, grossWeightTons);
        this.lengthMeters = Math.max(2.0, lengthMeters);
        this.maxSpeedKmh = Math.max(10.0, maxSpeedKmh);
        this.brakeStatus = brakeStatus != null ? brakeStatus : BrakeStatus.OPERATIONAL;
        this.couplerType = couplerType != null ? couplerType : CouplerType.AAR_TYPE_E;
        this.rfidTag = rfidTag != null ? rfidTag : ("AEI-" + vehicleId);
        this.builtYear = builtYear > 1900 ? builtYear : 2015;
        this.inService = true;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getReportingMark() {
        return reportingMark;
    }

    public String getRoadNumber() {
        return roadNumber;
    }

    public CarType getCarType() {
        return carType;
    }

    public double getTareWeightTons() {
        return tareWeightTons;
    }

    public double getGrossWeightTons() {
        return grossWeightTons;
    }

    public double getCargoWeightTons() {
        return Math.max(0.0, grossWeightTons - tareWeightTons);
    }

    public double getLengthMeters() {
        return lengthMeters;
    }

    public double getMaxSpeedKmh() {
        return maxSpeedKmh;
    }

    public BrakeStatus getBrakeStatus() {
        return brakeStatus;
    }

    public void setBrakeStatus(BrakeStatus brakeStatus) {
        this.brakeStatus = Objects.requireNonNull(brakeStatus, "brakeStatus must not be null");
    }

    public CouplerType getCouplerType() {
        return couplerType;
    }

    public void setCouplerType(CouplerType couplerType) {
        this.couplerType = Objects.requireNonNull(couplerType, "couplerType must not be null");
    }

    public String getRfidTag() {
        return rfidTag;
    }

    public int getBuiltYear() {
        return builtYear;
    }

    public boolean isInService() {
        return inService;
    }

    public void setInService(boolean inService) {
        this.inService = inService;
    }

    public boolean isOperativeBrake() {
        return brakeStatus != null && brakeStatus.isOperative();
    }

    public double getEffectiveBrakingWeightTons() {
        return grossWeightTons * (brakeStatus != null ? brakeStatus.getEfficiencyFactor() : 0.0);
    }

    public abstract String getCargoDescription();

    public abstract HazmatClass getHazmatClass();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RollingStock that)) return false;
        return Objects.equals(vehicleId, that.vehicleId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(vehicleId);
    }

    @Override
    public String toString() {
        return String.format("%s [%s] - Gross: %.1f T, Len: %.1fm", vehicleId, carType, grossWeightTons, lengthMeters);
    }
}
