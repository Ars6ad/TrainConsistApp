package com.railconsist.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Aggregate Root representing an ordered rail vehicle consist.
 * Sequence 0 is the Lead Unit (Head End), Sequence N-1 is the Rear (End of Train).
 */
public class TrainConsist {
    private final String consistId;
    private String trainSymbol;
    private String originStation;
    private String destinationStation;
    private Instant createdAt;
    private final List<RollingStock> vehicles;

    public TrainConsist(String consistId, String trainSymbol, String originStation, String destinationStation) {
        this.consistId = Objects.requireNonNull(consistId, "consistId must not be null");
        this.trainSymbol = Objects.requireNonNull(trainSymbol, "trainSymbol must not be null");
        this.originStation = Objects.requireNonNull(originStation, "originStation must not be null");
        this.destinationStation = Objects.requireNonNull(destinationStation, "destinationStation must not be null");
        this.createdAt = Instant.now();
        this.vehicles = new ArrayList<>();
    }

    public String getConsistId() {
        return consistId;
    }

    public String getTrainSymbol() {
        return trainSymbol;
    }

    public void setTrainSymbol(String trainSymbol) {
        this.trainSymbol = Objects.requireNonNull(trainSymbol, "trainSymbol must not be null");
    }

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = Objects.requireNonNull(originStation, "originStation must not be null");
    }

    public String getDestinationStation() {
        return destinationStation;
    }

    public void setDestinationStation(String destinationStation) {
        this.destinationStation = Objects.requireNonNull(destinationStation, "destinationStation must not be null");
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public synchronized void addVehicle(RollingStock vehicle) {
        vehicles.add(Objects.requireNonNull(vehicle, "vehicle must not be null"));
    }

    public synchronized void addVehicleAt(int index, RollingStock vehicle) {
        Objects.requireNonNull(vehicle, "vehicle must not be null");
        if (index < 0 || index > vehicles.size()) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " for size " + vehicles.size());
        }
        vehicles.add(index, vehicle);
    }

    public synchronized RollingStock removeVehicle(int index) {
        if (index < 0 || index >= vehicles.size()) {
            throw new IndexOutOfBoundsException("Invalid index " + index + " for size " + vehicles.size());
        }
        return vehicles.remove(index);
    }

    public synchronized boolean removeVehicle(RollingStock vehicle) {
        return vehicles.remove(vehicle);
    }

    public synchronized void swapVehicles(int indexA, int indexB) {
        Collections.swap(vehicles, indexA, indexB);
    }

    public synchronized void reverseConsist() {
        Collections.reverse(vehicles);
    }

    public synchronized List<RollingStock> getVehicles() {
        return Collections.unmodifiableList(new ArrayList<>(vehicles));
    }

    public synchronized int getVehicleCount() {
        return vehicles.size();
    }

    public synchronized boolean isEmpty() {
        return vehicles.isEmpty();
    }

    public synchronized RollingStock getVehicle(int index) {
        return vehicles.get(index);
    }

    public synchronized Optional<Locomotive> getLeadLocomotive() {
        for (RollingStock vehicle : vehicles) {
            if (vehicle instanceof Locomotive loc && loc.isLeadUnit()) {
                return Optional.of(loc);
            }
        }
        for (RollingStock vehicle : vehicles) {
            if (vehicle instanceof Locomotive loc) {
                return Optional.of(loc);
            }
        }
        return Optional.empty();
    }

    public synchronized List<Locomotive> getLocomotives() {
        List<Locomotive> locos = new ArrayList<>();
        for (RollingStock v : vehicles) {
            if (v instanceof Locomotive loc) {
                locos.add(loc);
            }
        }
        return locos;
    }

    public synchronized List<FreightCar> getFreightCars() {
        List<FreightCar> freightCars = new ArrayList<>();
        for (RollingStock v : vehicles) {
            if (v instanceof FreightCar fc) {
                freightCars.add(fc);
            }
        }
        return freightCars;
    }

    public synchronized RollingStock getRearVehicle() {
        if (vehicles.isEmpty()) {
            return null;
        }
        return vehicles.get(vehicles.size() - 1);
    }

    public synchronized double getTotalGrossWeightTons() {
        return vehicles.stream().mapToDouble(RollingStock::getGrossWeightTons).sum();
    }

    public synchronized double getTotalLengthMeters() {
        return vehicles.stream().mapToDouble(RollingStock::getLengthMeters).sum();
    }

    public synchronized double getTotalLengthFeet() {
        return getTotalLengthMeters() * 3.28084;
    }

    public synchronized int getTotalHorsepower() {
        int hp = 0;
        for (RollingStock v : vehicles) {
            if (v instanceof Locomotive loc) {
                hp += loc.getHorsepower();
            }
        }
        return hp;
    }

    @Override
    public String toString() {
        return String.format("TrainConsist[%s, Symbol=%s, %d vehicles, Origin=%s -> Dest=%s]",
                consistId, trainSymbol, vehicles.size(), originStation, destinationStation);
    }
}
