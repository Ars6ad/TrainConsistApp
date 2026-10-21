package com.railconsist.service;

import com.railconsist.model.*;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Enterprise search, indexing, filtering, and aggregation service for rolling stock.
 * Implements linear search, binary search, regex validation, set uniqueness, and stream analytics.
 */
public class ConsistSearchAndInventoryService {

    // Regex patterns for railway industry equipment reporting marks (AAR standard):
    // e.g. "BNSF-102934", "TTX-554210", "UP-8842", "AMTK-156"
    private static final Pattern EQUIPMENT_ID_PATTERN = Pattern.compile("^[A-Z]{2,5}-\\d{3,6}$");

    // UN Hazardous Materials Code pattern:
    // e.g. "UN1017" (Chlorine), "UN1075" (Liquefied petroleum gas), "UN1203" (Gasoline)
    private static final Pattern HAZMAT_UN_PATTERN = Pattern.compile("^UN\\d{4}$");

    /**
     * Validates whether an equipment identifier complies with standard reporting mark conventions (UC11).
     */
    public boolean isValidEquipmentId(String equipmentId) {
        if (equipmentId == null) return false;
        return EQUIPMENT_ID_PATTERN.matcher(equipmentId.trim()).matches();
    }

    /**
     * Validates whether a hazardous material code complies with the UN 4-digit standard (UC11).
     */
    public boolean isValidHazmatCode(String hazmatCode) {
        if (hazmatCode == null) return false;
        return HAZMAT_UN_PATTERN.matcher(hazmatCode.trim()).matches();
    }

    /**
     * Extracts unique equipment IDs using HashSet (UC3).
     */
    public Set<String> getUniqueVehicleIds(TrainConsist consist) {
        if (consist == null) return Collections.emptySet();
        Set<String> uniqueIds = new HashSet<>();
        for (RollingStock v : consist.getVehicles()) {
            uniqueIds.add(v.getVehicleId());
        }
        return uniqueIds;
    }

    /**
     * Extracts unique equipment IDs in natural ascending order using TreeSet (UC4).
     */
    public TreeSet<String> getSortedUniqueVehicleIds(TrainConsist consist) {
        if (consist == null) return new TreeSet<>();
        TreeSet<String> sortedIds = new TreeSet<>();
        for (RollingStock v : consist.getVehicles()) {
            sortedIds.add(v.getVehicleId());
        }
        return sortedIds;
    }

    /**
     * Creates a lookup map of vehicle ID to gross weight or capacity (UC6).
     */
    public Map<String, Double> mapVehicleTonnages(TrainConsist consist) {
        if (consist == null) return Collections.emptyMap();
        Map<String, Double> map = new HashMap<>();
        for (RollingStock v : consist.getVehicles()) {
            map.put(v.getVehicleId(), v.getGrossWeightTons());
        }
        return map;
    }

    /**
     * Filters rolling stock weighing more than the specified threshold using Java Streams (UC8).
     */
    public List<RollingStock> filterHeavyVehicles(TrainConsist consist, double minWeightTons) {
        if (consist == null) return Collections.emptyList();
        return consist.getVehicles().stream()
                .filter(v -> v.getGrossWeightTons() >= minWeightTons)
                .collect(Collectors.toList());
    }

    /**
     * Groups rolling stock by their CarType category using Streams (UC9).
     */
    public Map<CarType, List<RollingStock>> groupVehiclesByType(TrainConsist consist) {
        if (consist == null) return Collections.emptyMap();
        return consist.getVehicles().stream()
                .collect(Collectors.groupingBy(RollingStock::getCarType));
    }

    /**
     * Calculates total passenger seating capacity using Stream reduce (UC10).
     */
    public int calculateTotalPassengerCapacity(TrainConsist consist) {
        if (consist == null) return 0;
        return consist.getVehicles().stream()
                .filter(v -> v instanceof PassengerCoach)
                .map(v -> ((PassengerCoach) v).getSeatingCapacity())
                .reduce(0, Integer::sum);
    }

    /**
     * Performs a linear search for a vehicle by ID (UC18).
     * Throws IllegalStateException if the consist is empty (UC20).
     */
    public Optional<RollingStock> linearSearch(TrainConsist consist, String targetId) {
        if (consist == null || consist.isEmpty()) {
            throw new IllegalStateException("Cannot search on an empty consist! (UC20)");
        }
        if (targetId == null) return Optional.empty();

        for (RollingStock v : consist.getVehicles()) {
            if (v.getVehicleId().equalsIgnoreCase(targetId.trim())) {
                return Optional.of(v);
            }
        }
        return Optional.empty();
    }

    /**
     * Performs a high-performance logarithmic Binary Search over a sorted array of rolling stock (UC19).
     * Throws IllegalStateException if consist is empty (UC20).
     */
    public int binarySearch(RollingStock[] sortedVehicles, String targetId) {
        if (sortedVehicles == null || sortedVehicles.length == 0) {
            throw new IllegalStateException("Cannot binary search on empty array! (UC20)");
        }
        if (targetId == null) return -1;

        int low = 0;
        int high = sortedVehicles.length - 1;

        while (low <= high) {
            int mid = low + (high - low) / 2;
            int cmp = targetId.compareToIgnoreCase(sortedVehicles[mid].getVehicleId());

            if (cmp == 0) {
                return mid; // Found
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        return -1; // Not found
    }

    /**
     * Sorts rolling stock list by gross tonnage ascending (UC7).
     */
    public List<RollingStock> sortByGrossWeight(TrainConsist consist) {
        if (consist == null) return Collections.emptyList();
        List<RollingStock> list = new ArrayList<>(consist.getVehicles());
        list.sort(Comparator.comparingDouble(RollingStock::getGrossWeightTons));
        return list;
    }

    /**
     * Sorts rolling stock list alphabetically by vehicle ID (UC17).
     */
    public List<RollingStock> sortByVehicleIdAlphabetical(TrainConsist consist) {
        if (consist == null) return Collections.emptyList();
        List<RollingStock> list = new ArrayList<>(consist.getVehicles());
        list.sort(Comparator.comparing(RollingStock::getVehicleId));
        return list;
    }
}
