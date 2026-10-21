package com.railconsist.model;

/**
 * Modern Flashing Rear-End Device (FRED) / End of Train Device (EOTD).
 * Provides continuous two-way brake pipe pressure telemetry and rear visibility.
 */
public class EndOfTrainDevice extends RollingStock {
    private double batteryPercent;
    private boolean markerLightFlashing;
    private boolean telemetryRadioLinked;
    private double brakePipePressurePsi;
    private boolean motionDetected;

    public EndOfTrainDevice(
            String vehicleId,
            String reportingMark,
            String roadNumber
    ) {
        super(
                vehicleId,
                reportingMark,
                roadNumber,
                CarType.END_OF_TRAIN_DEVICE,
                0.035, // ~35 kg telemetry unit
                0.035,
                0.5,
                140.0,
                BrakeStatus.OPERATIONAL,
                CouplerType.AAR_TYPE_E,
                "EOTD-" + vehicleId,
                2024
        );
        this.batteryPercent = 98.0;
        this.markerLightFlashing = true;
        this.telemetryRadioLinked = true;
        this.brakePipePressurePsi = 90.0;
        this.motionDetected = true;
    }

    public double getBatteryPercent() {
        return batteryPercent;
    }

    public void setBatteryPercent(double batteryPercent) {
        this.batteryPercent = Math.max(0.0, Math.min(100.0, batteryPercent));
    }

    public boolean isMarkerLightFlashing() {
        return markerLightFlashing;
    }

    public void setMarkerLightFlashing(boolean markerLightFlashing) {
        this.markerLightFlashing = markerLightFlashing;
    }

    public boolean isTelemetryRadioLinked() {
        return telemetryRadioLinked;
    }

    public void setTelemetryRadioLinked(boolean telemetryRadioLinked) {
        this.telemetryRadioLinked = telemetryRadioLinked;
    }

    public double getBrakePipePressurePsi() {
        return brakePipePressurePsi;
    }

    public void setBrakePipePressurePsi(double brakePipePressurePsi) {
        this.brakePipePressurePsi = Math.max(0.0, brakePipePressurePsi);
    }

    public boolean isMotionDetected() {
        return motionDetected;
    }

    public void setMotionDetected(boolean motionDetected) {
        this.motionDetected = motionDetected;
    }

    @Override
    public String getCargoDescription() {
        return String.format("EOTD Telemetry (Batt: %.0f%%, BP: %.1f PSI, Radio: %s)",
                batteryPercent, brakePipePressurePsi, telemetryRadioLinked ? "ONLINE" : "OFFLINE");
    }

    @Override
    public HazmatClass getHazmatClass() {
        return HazmatClass.NONE;
    }
}
