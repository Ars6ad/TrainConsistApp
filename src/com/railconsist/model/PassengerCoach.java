package com.railconsist.model;

/**
 * Passenger rail coach vehicle.
 */
public class PassengerCoach extends RollingStock {
    public enum CoachClass {
        COACH,
        BUSINESS,
        FIRST_CLASS,
        SLEEPER,
        DINER,
        OBSERVATION
    }

    private final int seatingCapacity;
    private int passengerCount;
    private final CoachClass coachClass;
    private final double headEndPowerDrawKW;

    public PassengerCoach(
            String vehicleId,
            String reportingMark,
            String roadNumber,
            double tareWeightTons,
            double lengthMeters,
            double maxSpeedKmh,
            int seatingCapacity,
            int passengerCount,
            CoachClass coachClass
    ) {
        super(
                vehicleId,
                reportingMark,
                roadNumber,
                CarType.PASSENGER_COACH,
                tareWeightTons,
                tareWeightTons + (passengerCount * 0.08), // average passenger + luggage = 80kg (0.08 tons)
                lengthMeters,
                maxSpeedKmh,
                BrakeStatus.OPERATIONAL,
                CouplerType.TIGHTLOCK,
                "AEI-PAX-" + vehicleId,
                2021
        );
        this.seatingCapacity = Math.max(1, seatingCapacity);
        this.passengerCount = Math.min(this.seatingCapacity, Math.max(0, passengerCount));
        this.coachClass = coachClass != null ? coachClass : CoachClass.COACH;
        this.headEndPowerDrawKW = 45.0;
    }

    public int getSeatingCapacity() {
        return seatingCapacity;
    }

    public int getPassengerCount() {
        return passengerCount;
    }

    public void setPassengerCount(int count) {
        this.passengerCount = Math.min(seatingCapacity, Math.max(0, count));
        this.grossWeightTons = this.tareWeightTons + (this.passengerCount * 0.08);
    }

    public CoachClass getCoachClass() {
        return coachClass;
    }

    public double getHeadEndPowerDrawKW() {
        return headEndPowerDrawKW;
    }

    @Override
    public String getCargoDescription() {
        return String.format("%s (%d/%d Passengers)", coachClass, passengerCount, seatingCapacity);
    }

    @Override
    public HazmatClass getHazmatClass() {
        return HazmatClass.NONE;
    }
}
