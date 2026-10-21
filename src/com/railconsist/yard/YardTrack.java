package com.railconsist.yard;

import com.railconsist.model.RollingStock;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Represents a physical railway track in a yard or siding.
 * Uses a LinkedList (supporting LIFO/FIFO shunting moves from either head or tail).
 */
public class YardTrack {

    private final String trackId;
    private final String trackName;
    private final double maxCapacityMeters;
    private final LinkedList<RollingStock> vehiclesOnTrack;

    public YardTrack(String trackId, String trackName, double maxCapacityMeters) {
        this.trackId = Objects.requireNonNull(trackId, "trackId cannot be null");
        this.trackName = Objects.requireNonNull(trackName, "trackName cannot be null");
        this.maxCapacityMeters = maxCapacityMeters;
        this.vehiclesOnTrack = new LinkedList<>();
    }

    public synchronized String getTrackId() {
        return trackId;
    }

    public synchronized String getTrackName() {
        return trackName;
    }

    public synchronized double getMaxCapacityMeters() {
        return maxCapacityMeters;
    }

    public synchronized int getVehicleCount() {
        return vehiclesOnTrack.size();
    }

    public synchronized double getCurrentLengthMeters() {
        return vehiclesOnTrack.stream().mapToDouble(RollingStock::getLengthMeters).sum();
    }

    public synchronized double getCurrentWeightTons() {
        return vehiclesOnTrack.stream().mapToDouble(RollingStock::getGrossWeightTons).sum();
    }

    public synchronized boolean canAcceptVehicle(RollingStock vehicle) {
        if (vehicle == null) return false;
        return (getCurrentLengthMeters() + vehicle.getLengthMeters()) <= maxCapacityMeters;
    }

    public synchronized void pushToHead(RollingStock vehicle) {
        if (!canAcceptVehicle(vehicle)) {
            throw new IllegalStateException("Track " + trackId + " capacity exceeded (max: " + maxCapacityMeters + "m)");
        }
        vehiclesOnTrack.addFirst(vehicle);
    }

    public synchronized void pushToTail(RollingStock vehicle) {
        if (!canAcceptVehicle(vehicle)) {
            throw new IllegalStateException("Track " + trackId + " capacity exceeded (max: " + maxCapacityMeters + "m)");
        }
        vehiclesOnTrack.addLast(vehicle);
    }

    public synchronized RollingStock popFromHead() {
        if (vehiclesOnTrack.isEmpty()) {
            throw new IllegalStateException("Track " + trackId + " is empty; cannot pop from head.");
        }
        return vehiclesOnTrack.removeFirst();
    }

    public synchronized RollingStock popFromTail() {
        if (vehiclesOnTrack.isEmpty()) {
            throw new IllegalStateException("Track " + trackId + " is empty; cannot pop from tail.");
        }
        return vehiclesOnTrack.removeLast();
    }

    public synchronized List<RollingStock> getVehicles() {
        return Collections.unmodifiableList(new LinkedList<>(vehiclesOnTrack));
    }

    public synchronized void clear() {
        vehiclesOnTrack.clear();
    }

    @Override
    public synchronized String toString() {
        return String.format("YardTrack[%s '%s': %d cars, %.1f/%.1fm (%.1f%% full), %.1f tons]",
                trackId, trackName, vehiclesOnTrack.size(), getCurrentLengthMeters(), maxCapacityMeters,
                (maxCapacityMeters > 0 ? (getCurrentLengthMeters() / maxCapacityMeters) * 100.0 : 0.0),
                getCurrentWeightTons());
    }
}
