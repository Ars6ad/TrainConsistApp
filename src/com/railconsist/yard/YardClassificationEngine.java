package com.railconsist.yard;

import com.railconsist.model.FreightCar;
import com.railconsist.model.Locomotive;
import com.railconsist.model.RollingStock;
import com.railconsist.model.TrainConsist;

import java.util.*;

/**
 * Enterprise classification yard switching and hump sorting engine.
 * Supports track-to-track shunting, gravity hump rolling, and consist re-marshalling.
 */
public class YardClassificationEngine {

    private final String yardCode;
    private final String yardName;
    private final Map<String, YardTrack> tracks;
    private final List<String> operationLog;

    public YardClassificationEngine(String yardCode, String yardName) {
        this.yardCode = yardCode;
        this.yardName = yardName;
        this.tracks = new LinkedHashMap<>();
        this.operationLog = new ArrayList<>();
        initializeDefaultTracks();
    }

    private void initializeDefaultTracks() {
        addTrack(new YardTrack("ARR-01", "Receiving & Inbound Track 1", 1800.0));
        addTrack(new YardTrack("CLASS-01", "Intermodal / Priority Track", 1200.0));
        addTrack(new YardTrack("CLASS-02", "Hazardous & Chemicals Track", 1200.0));
        addTrack(new YardTrack("CLASS-03", "Bulk Minerals & Coal Track", 1200.0));
        addTrack(new YardTrack("CLASS-04", "General Merchandise Track", 1200.0));
        addTrack(new YardTrack("DEP-01", "Outbound Departure Track 1", 2000.0));
    }

    public synchronized void addTrack(YardTrack track) {
        tracks.put(track.getTrackId(), track);
    }

    public synchronized YardTrack getTrack(String trackId) {
        return tracks.get(trackId);
    }

    public synchronized Collection<YardTrack> getAllTracks() {
        return Collections.unmodifiableCollection(tracks.values());
    }

    public synchronized List<String> getOperationLog() {
        return Collections.unmodifiableList(new ArrayList<>(operationLog));
    }

    /**
     * Receives an inbound train consist into a specified receiving track.
     */
    public synchronized void receiveConsist(TrainConsist consist, String targetTrackId) {
        YardTrack track = tracks.get(targetTrackId);
        if (track == null) {
            throw new IllegalArgumentException("Unknown yard track: " + targetTrackId);
        }

        for (RollingStock vehicle : consist.getVehicles()) {
            track.pushToTail(vehicle);
        }
        logOperation(String.format("Consist %s received onto track %s (%d vehicles, %.1f tons)",
                consist.getConsistId(), targetTrackId, consist.getVehicleCount(), consist.getTotalGrossWeightTons()));
    }

    /**
     * Shunts a single car from the head or tail of a source track to a destination track.
     */
    public synchronized void shuntCar(String sourceTrackId, boolean fromHead, String destTrackId, boolean toTail) {
        YardTrack source = tracks.get(sourceTrackId);
        YardTrack dest = tracks.get(destTrackId);
        if (source == null || dest == null) {
            throw new IllegalArgumentException("Invalid track IDs specified for shunting.");
        }

        RollingStock car = fromHead ? source.popFromHead() : source.popFromTail();
        if (toTail) {
            dest.pushToTail(car);
        } else {
            dest.pushToHead(car);
        }

        logOperation(String.format("Shunted car %s (%s) from %s (%s) to %s (%s)",
                car.getVehicleId(), car.getCarType(), sourceTrackId, fromHead ? "head" : "tail",
                destTrackId, toTail ? "tail" : "head"));
    }

    /**
     * Hump Yard Automated Sorting:
     * Uncouples the inbound track vehicles and rolls them downhill into classification tracks
     * based on vehicle type and hazmat hazard rating.
     */
    public synchronized Map<String, Integer> executeHumpSort(String inboundTrackId) {
        YardTrack inbound = tracks.get(inboundTrackId);
        if (inbound == null) {
            throw new IllegalArgumentException("Inbound track not found: " + inboundTrackId);
        }

        Map<String, Integer> classificationCounts = new HashMap<>();
        while (inbound.getVehicleCount() > 0) {
            RollingStock car = inbound.popFromHead();
            String targetTrackId;

            if (car instanceof Locomotive) {
                targetTrackId = "DEP-01";
            } else if (car.getHazmatClass() != null && car.getHazmatClass().isHazardous()) {
                targetTrackId = "CLASS-02"; // Hazmat
            } else if (car.getCarType() == com.railconsist.model.CarType.INTERMODAL_WELL) {
                targetTrackId = "CLASS-01"; // Intermodal
            } else if (car.getCarType() == com.railconsist.model.CarType.COVERED_HOPPER ||
                    car.getCarType() == com.railconsist.model.CarType.OPEN_HOPPER) {
                targetTrackId = "CLASS-03"; // Bulk
            } else {
                targetTrackId = "CLASS-04"; // General
            }

            YardTrack target = tracks.get(targetTrackId);
            if (target != null) {
                target.pushToTail(car);
                classificationCounts.merge(targetTrackId, 1, Integer::sum);
            }
        }

        logOperation("Hump classification completed for inbound track: " + inboundTrackId);
        return classificationCounts;
    }

    /**
     * Assembles an outbound consist from a departure track.
     */
    public synchronized TrainConsist assembleOutboundConsist(String consistId, String symbol, String origin, String destination, String departureTrackId) {
        YardTrack depTrack = tracks.get(departureTrackId);
        if (depTrack == null || depTrack.getVehicleCount() == 0) {
            throw new IllegalStateException("Departure track is empty or missing: " + departureTrackId);
        }

        TrainConsist consist = new TrainConsist(consistId, symbol, origin, destination);
        while (depTrack.getVehicleCount() > 0) {
            consist.addVehicle(depTrack.popFromHead());
        }

        logOperation(String.format("Outbound consist %s (%s) assembled with %d vehicles.",
                consistId, symbol, consist.getVehicleCount()));
        return consist;
    }

    /**
     * Sorts vehicles on a track using the Classic Bubble Sort algorithm (Demonstrating UC16).
     */
    public synchronized void sortTrackByWeightBubbleSort(String trackId, boolean descending) {
        YardTrack track = tracks.get(trackId);
        if (track == null || track.getVehicleCount() <= 1) return;

        List<RollingStock> list = new ArrayList<>(track.getVehicles());
        int n = list.size();

        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                boolean swapNeeded = descending ?
                        (list.get(j).getGrossWeightTons() < list.get(j + 1).getGrossWeightTons()) :
                        (list.get(j).getGrossWeightTons() > list.get(j + 1).getGrossWeightTons());

                if (swapNeeded) {
                    RollingStock temp = list.get(j);
                    list.set(j, list.get(j + 1));
                    list.set(j + 1, temp);
                }
            }
        }

        track.clear();
        for (RollingStock v : list) {
            track.pushToTail(v);
        }
        logOperation(String.format("Track %s sorted by weight using Bubble Sort (descending=%b)", trackId, descending));
    }

    private void logOperation(String msg) {
        operationLog.add("[" + new Date() + "] " + msg);
    }

    public String getYardCode() {
        return yardCode;
    }

    public String getYardName() {
        return yardName;
    }
}
