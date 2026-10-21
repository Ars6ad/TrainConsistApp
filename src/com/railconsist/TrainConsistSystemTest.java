package com.railconsist;

import com.railconsist.model.*;
import com.railconsist.physics.*;
import com.railconsist.rules.*;
import com.railconsist.service.*;
import com.railconsist.yard.*;

import java.util.List;
import java.util.Optional;

/**
 * Enterprise automated integration test suite validating domain integrity,
 * physics dynamics calculations, marshalling safety compliance rules, and search algorithms.
 */
public class TrainConsistSystemTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;

    public static void main(String[] args) {
        System.out.println("========================================================================");
        System.out.println("     RUNNING RAILCONSIST ENTERPRISE AUTOMATED INTEGRATION TESTS         ");
        System.out.println("========================================================================");

        testConsistDomainMetrics();
        testHazmatBufferRuleCompliance();
        testBrakingPercentageCompliance();
        testStringLiningCurvingRiskDetection();
        testEndOfTrainTelemetryPlacement();
        testYardClassificationAndSorting();
        testRegexValidation();
        testLinearAndBinarySearch();
        testExceptionBoundaries();

        System.out.println("========================================================================");
        System.out.printf("TEST SUITE RESULTS: %d/%d PASSED (%s)%n",
                testsPassed, testsRun, (testsPassed == testsRun ? "100% SUCCESS" : "FAILURES DETECTED"));
        System.out.println("========================================================================");

        if (testsPassed != testsRun) {
            System.exit(1);
        }
    }

    private static void assertTrue(String testName, boolean condition, String failureMsg) {
        testsRun++;
        if (condition) {
            testsPassed++;
            System.out.println("  [PASS] " + testName);
        } else {
            System.err.println("  [FAIL] " + testName + " -> " + failureMsg);
        }
    }

    private static void testConsistDomainMetrics() {
        TrainConsist train = TrainConsistBuilder.buildIntermodalTranscon();
        assertTrue("Intermodal vehicle count check", train.getVehicleCount() == 18,
                "Expected 18 vehicles, got " + train.getVehicleCount());

        TractiveEffortCalculator calc = new TractiveEffortCalculator();
        ConsistMetrics metrics = calc.computeMetrics(train);

        assertTrue("Consist horsepower calculation", metrics.totalHorsepower() == (3 * 4400),
                "Expected 13,200 HP, got " + metrics.totalHorsepower());
        assertTrue("Consist operative brake compliance", metrics.isMeetsBrakeRegulations(),
                "Expected operative brake percentage >= 85%, got " + metrics.operativeBrakePercentage());
        assertTrue("Locomotive count check", metrics.locomotiveCount() == 3,
                "Expected 3 locomotives, got " + metrics.locomotiveCount());
    }

    private static void testHazmatBufferRuleCompliance() {
        MarshallingRuleEngine engine = new MarshallingRuleEngine();

        // 1. Compliant train
        TrainConsist safeChem = TrainConsistBuilder.buildChemicalManifest();
        SafetyAuditReport safeReport = engine.auditConsist(safeChem);
        assertTrue("Chemical manifest hazmat buffer compliance",
                safeReport.violations().stream().noneMatch(v -> v.ruleCode().contains("174.85")),
                "Expected no hazmat violations on compliant chemical train");

        // 2. Non-compliant train
        TrainConsist hazardTrain = TrainConsistBuilder.buildHazardsDemoTrain();
        SafetyAuditReport hazardReport = engine.auditConsist(hazardTrain);
        assertTrue("Hazmat violation detection on hazard train",
                hazardReport.violations().stream().anyMatch(v -> v.ruleCode().contains("174.85")),
                "Expected hazmat buffer violation to be caught on unbuffered toxic gas car");
    }

    private static void testBrakingPercentageCompliance() {
        MarshallingRuleEngine engine = new MarshallingRuleEngine();
        TrainConsist hazardTrain = TrainConsistBuilder.buildHazardsDemoTrain();
        SafetyAuditReport report = engine.auditConsist(hazardTrain);

        assertTrue("Defective brake percentage detection",
                report.violations().stream().anyMatch(v -> v.ruleCode().equals("FRA-49CFR-232")),
                "Expected defective brake violation to be flagged");
    }

    private static void testStringLiningCurvingRiskDetection() {
        WeightDistributionAnalyzer analyzer = new WeightDistributionAnalyzer();
        TrainConsist hazardTrain = TrainConsistBuilder.buildHazardsDemoTrain();
        WeightDistributionAnalyzer.DistributionReport report = analyzer.analyze(hazardTrain);

        assertTrue("String-lining risk anomaly detected",
                !report.detectedAnomalies().isEmpty(),
                "Expected string-lining risk when 35 heavy coal cars trail a 22-ton empty flatcar");
        assertTrue("High curvature risk score",
                report.overallCurvingRiskScore() > 0.5,
                "Expected risk score > 0.5, got " + report.overallCurvingRiskScore());
    }

    private static void testEndOfTrainTelemetryPlacement() {
        MarshallingRuleEngine engine = new MarshallingRuleEngine();
        TrainConsist hazardTrain = TrainConsistBuilder.buildHazardsDemoTrain();
        SafetyAuditReport report = engine.auditConsist(hazardTrain);

        assertTrue("Missing EOT device detection",
                report.violations().stream().anyMatch(v -> v.ruleCode().contains("232.403")),
                "Expected missing rear telemetry device violation");
    }

    private static void testYardClassificationAndSorting() {
        YardClassificationEngine yard = new YardClassificationEngine("TEST-YD", "Test Switching Yard");
        yard.receiveConsist(TrainConsistBuilder.buildIntermodalTranscon(), "ARR-01");

        YardTrack inbound = yard.getTrack("ARR-01");
        int initialCount = inbound.getVehicleCount();
        assertTrue("Inbound track vehicle loading", initialCount > 0, "Inbound track empty after receiving consist");

        yard.executeHumpSort("ARR-01");
        assertTrue("Inbound track cleared after hump sort", inbound.getVehicleCount() == 0,
                "Expected inbound track to be 0 after hump sort");

        // Bubble sort test
        YardTrack sortTrack = new YardTrack("SORT-01", "Sort", 1000.0);
        sortTrack.pushToTail(new FreightCar("C3", "TEST", "3", CarType.BOXCAR, 30.0, 90.0, 80.0, 16.0, 90.0, "Cargo", HazmatClass.NONE, null, "Yard"));
        sortTrack.pushToTail(new FreightCar("C1", "TEST", "1", CarType.BOXCAR, 30.0, 90.0, 10.0, 16.0, 90.0, "Cargo", HazmatClass.NONE, null, "Yard"));
        sortTrack.pushToTail(new FreightCar("C2", "TEST", "2", CarType.BOXCAR, 30.0, 90.0, 45.0, 16.0, 90.0, "Cargo", HazmatClass.NONE, null, "Yard"));

        yard.addTrack(sortTrack);
        yard.sortTrackByWeightBubbleSort("SORT-01", false);

        List<RollingStock> sortedCars = sortTrack.getVehicles();
        assertTrue("Bubble sort ascending order verification",
                sortedCars.get(0).getGrossWeightTons() <= sortedCars.get(1).getGrossWeightTons() &&
                        sortedCars.get(1).getGrossWeightTons() <= sortedCars.get(2).getGrossWeightTons(),
                "Bubble sort failed to sort cars in ascending weight order");
    }

    private static void testRegexValidation() {
        ConsistSearchAndInventoryService service = new ConsistSearchAndInventoryService();
        assertTrue("Valid equipment mark UP-8842", service.isValidEquipmentId("UP-8842"), "Failed UP-8842");
        assertTrue("Valid equipment mark BNSF-7489", service.isValidEquipmentId("BNSF-7489"), "Failed BNSF-7489");
        assertTrue("Invalid equipment mark with symbols", !service.isValidEquipmentId("INVALID*CAR"), "Failed symbol rejection");
        assertTrue("Valid UN Hazmat UN1017", service.isValidHazmatCode("UN1017"), "Failed UN1017");
        assertTrue("Invalid UN Hazmat code", !service.isValidHazmatCode("HAZ-99"), "Failed invalid UN rejection");
    }

    private static void testLinearAndBinarySearch() {
        ConsistSearchAndInventoryService service = new ConsistSearchAndInventoryService();
        TrainConsist train = TrainConsistBuilder.buildIntermodalTranscon();

        Optional<RollingStock> foundLinear = service.linearSearch(train, "BNSF-7489");
        assertTrue("Linear search found existing lead unit", foundLinear.isPresent(), "Could not find BNSF-7489");

        Optional<RollingStock> notFound = service.linearSearch(train, "NON-EXISTENT-CAR");
        assertTrue("Linear search non-existent returns empty", notFound.isEmpty(), "Should not find NON-EXISTENT-CAR");

        List<RollingStock> sorted = service.sortByVehicleIdAlphabetical(train);
        RollingStock[] arr = sorted.toArray(new RollingStock[0]);
        int idx = service.binarySearch(arr, "BNSF-7489");
        assertTrue("Binary search found correct element", idx >= 0 && arr[idx].getVehicleId().equals("BNSF-7489"),
                "Binary search failed on BNSF-7489");

        int missingIdx = service.binarySearch(arr, "ZZZZ-9999");
        assertTrue("Binary search missing element returns -1", missingIdx == -1, "Expected -1 for missing car");
    }

    private static void testExceptionBoundaries() {
        ConsistSearchAndInventoryService service = new ConsistSearchAndInventoryService();
        TrainConsist empty = new TrainConsist("EMPTY", "EMP", "A", "B");

        boolean caughtEmpty = false;
        try {
            service.linearSearch(empty, "TEST");
        } catch (IllegalStateException e) {
            caughtEmpty = true;
        }
        assertTrue("Empty train search throws IllegalStateException (UC20)", caughtEmpty, "Expected exception");
    }
}
