package com.railconsist;

import com.railconsist.model.*;
import com.railconsist.physics.*;
import com.railconsist.rules.*;
import com.railconsist.service.*;
import com.railconsist.yard.*;

import java.util.*;

/**
 * Enterprise Application Entry Point for RailConsist Enterprise 2.0.
 * Orchestrates consist management, physics dynamics calculations, regulatory compliance audits,
 * yard hump classification, and comprehensive showcases for Use Cases 1 through 20.
 */
public class TrainConsistApplication {

    private final MarshallingRuleEngine ruleEngine;
    private final TractiveEffortCalculator tractiveCalculator;
    private final WeightDistributionAnalyzer weightAnalyzer;
    private final StoppingDistanceEstimator stoppingEstimator;
    private final ConsistSearchAndInventoryService searchService;
    private final PerformanceBenchmarkService benchmarkService;
    private final YardClassificationEngine yardEngine;

    private TrainConsist activeConsist;

    public TrainConsistApplication() {
        this.ruleEngine = new MarshallingRuleEngine();
        this.tractiveCalculator = new TractiveEffortCalculator();
        this.weightAnalyzer = new WeightDistributionAnalyzer();
        this.stoppingEstimator = new StoppingDistanceEstimator();
        this.searchService = new ConsistSearchAndInventoryService();
        this.benchmarkService = new PerformanceBenchmarkService();
        this.yardEngine = new YardClassificationEngine("CHI-COR", "Chicago Corwith Terminal Yard");

        // Default active consist: Intermodal Transcon
        this.activeConsist = TrainConsistBuilder.buildIntermodalTranscon();
    }

    public static void main(String[] args) {
        TrainConsistApplication app = new TrainConsistApplication();

        if (args.length > 0 && args[0].equalsIgnoreCase("--batch")) {
            app.runAutomatedShowcase();
            return;
        }

        app.runInteractiveMenu();
    }

    public void runInteractiveMenu() {
        printBanner();
        Scanner scanner = new Scanner(System.in);

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("\nSelect an enterprise option (1-8, 0 to Exit): ");
            String input = scanner.hasNextLine() ? scanner.nextLine().trim() : "0";

            switch (input) {
                case "1" -> displayConsistOverview(activeConsist);
                case "2" -> runSafetyAudit(activeConsist);
                case "3" -> runPhysicsAndDynamicsAnalysis(activeConsist);
                case "4" -> runYardClassificationSimulation();
                case "5" -> runSearchAndInventoryAnalytics();
                case "6" -> runPerformanceBenchmark();
                case "7" -> switchActiveConsist(scanner);
                case "8" -> runAutomatedShowcase();
                case "0", "exit", "quit" -> {
                    System.out.println("\n[SYSTEM] Terminating RailConsist Enterprise. Operational log archived.");
                    running = false;
                }
                default -> System.out.println("\n[WARNING] Invalid choice. Please enter a valid number (1-8, 0).");
            }
        }
    }

    private void printBanner() {
        System.out.println("==========================================================================================");
        System.out.println("     ____       _ _  ____                 _     _     _____       _                       ");
        System.out.println("    |  _ \\ __ _(_) |/ ___|___  _ __  ___(_)___| |_  | ____|_ __ | |_ ___ _ __ _ __  _ __ ");
        System.out.println("    | |_) / _` | | | |   / _ \\| '_ \\/ __| / __| __| |  _| | '_ \\| __/ _ \\ '__| '_ \\| '__|");
        System.out.println("    |  _ < (_| | | | |__| (_) | | | \\__ \\ \\__ \\ |_  | |___| | | | ||  __/ |  | |_) | |   ");
        System.out.println("    |_| \\_\\__,_|_|_|\\____\\___/|_| |_|___/_|___/\\__| |_____|_| |_|\\__\\___|_|  | .__/|_|   ");
        System.out.println("                                                                              |_|         ");
        System.out.println("       Enterprise Train Consist Management, Yard Switching & Dynamics Physics System      ");
        System.out.println("==========================================================================================");
    }

    private void printMenu() {
        System.out.println("\n================================== [ MAIN OPERATIONS MENU ] ==================================");
        System.out.println(" [1] View Current Train Consist & Vehicle Manifest");
        System.out.println(" [2] Run Marshalling & Safety Regulatory Compliance Audit (FRA / AAR Rules)");
        System.out.println(" [3] Run Train Dynamics & Physics Engine (Davis Formula, Tractive Effort, Stopping)");
        System.out.println(" [4] Yard Operations: Gravity Hump Sorting & Shunting Simulator");
        System.out.println(" [5] Rolling Stock Search, Regex Validation & Inventory Analytics (UC3-UC20)");
        System.out.println(" [6] High-Speed Performance Profiler: Loops vs Java Streams Benchmark (UC13)");
        System.out.println(" [7] Load Different Train Preset (Intermodal, Chemical Manifest, Passenger, Hazard Demo)");
        System.out.println(" [8] Run Complete Automated 20 Use-Cases Showcase (UC1 through UC20)");
        System.out.println(" [0] Exit Application");
        System.out.println("==============================================================================================");
    }

    public void displayConsistOverview(TrainConsist consist) {
        System.out.println("\n-------------------------------- [ TRAIN CONSIST MANIFEST ] --------------------------------");
        System.out.println("Consist ID    : " + consist.getConsistId() + " (" + consist.getTrainSymbol() + ")");
        System.out.println("Route         : " + consist.getOriginStation() + " -> " + consist.getDestinationStation());
        System.out.println("Vehicle Count : " + consist.getVehicleCount() + " units");
        System.out.println("Gross Weight  : " + String.format("%.1f tons", consist.getTotalGrossWeightTons()));
        System.out.println("Total Length  : " + String.format("%.1f meters (%.1f feet)", consist.getTotalLengthMeters(), consist.getTotalLengthFeet()));
        System.out.println("Total Power   : " + consist.getTotalHorsepower() + " HP");
        System.out.println("--------------------------------------------------------------------------------------------");

        List<RollingStock> vehicles = consist.getVehicles();
        System.out.printf("%-4s | %-14s | %-22s | %-10s | %-10s | %-16s | %-12s%n",
                "POS", "EQUIPMENT ID", "TYPE", "WEIGHT(T)", "LENGTH(M)", "BRAKE STATUS", "HAZMAT PLACARD");
        System.out.println("--------------------------------------------------------------------------------------------");

        for (int i = 0; i < vehicles.size(); i++) {
            RollingStock v = vehicles.get(i);
            String hazmat = (v.getHazmatClass() != null && v.getHazmatClass().isHazardous()) ?
                    v.getHazmatClass().getPlacardCode() : "None";
            System.out.printf("#%-3d | %-14s | %-22s | %10.1f | %10.1f | %-16s | %-12s%n",
                    (i + 1), v.getVehicleId(), v.getCarType().name(), v.getGrossWeightTons(),
                    v.getLengthMeters(), v.getBrakeStatus().name(), hazmat);
        }
        System.out.println("--------------------------------------------------------------------------------------------");
    }

    public void runSafetyAudit(TrainConsist consist) {
        System.out.println("\n>>> EXECUTING SAFETY & REGULATORY MARSHALLING AUDIT...");
        SafetyAuditReport report = ruleEngine.auditConsist(consist);

        System.out.println("\n============================= [ SAFETY AUDIT REPORT ] =============================");
        System.out.println("Train Consist ID  : " + report.consistId());
        System.out.println("Audit Timestamp   : " + report.auditTimestamp());
        System.out.println("Dispatch Clearance: " + (report.isDispatchReady() ? "APPROVED (CLEAR TO DEPART)" : "REJECTED (HALT - SAFETY DEFECTS FOUND)"));
        System.out.println("Safety Score      : " + report.complianceScorePercent() + " / 100.0");
        System.out.println("Critical Halts    : " + report.criticalHaltsCount());
        System.out.println("Violations        : " + report.totalViolationsCount());
        System.out.println("Warnings          : " + report.totalWarningsCount());
        System.out.println("-----------------------------------------------------------------------------------");

        if (report.violations().isEmpty()) {
            System.out.println("[SUCCESS] Zero safety defects detected. All FRA/AAR marshalling rules satisfied.");
        } else {
            System.out.println("DETAILED SAFETY DEFECT FINDINGS:");
            for (RuleViolation f : report.violations()) {
                System.out.printf("[%s] Rule: %s (%s)%n  Description: %s%n  Action: %s%n%n",
                        f.severity(), f.ruleName(), f.ruleCode(), f.message(), f.remediationRecommendation());
            }
        }
        System.out.println("===================================================================================");
    }

    public void runPhysicsAndDynamicsAnalysis(TrainConsist consist) {
        System.out.println("\n>>> EXECUTING TRAIN DYNAMICS & DAVIS EQUATION PHYSICS ENGINE...");

        ConsistMetrics metrics = tractiveCalculator.computeMetrics(consist);
        System.out.println("\n========================== [ TRAIN DYNAMICS METRICS ] ==========================");
        System.out.printf("Gross Tonnage           : %,.1f tons (Trailing: %,.1f tons)%n", metrics.grossWeightTons(), metrics.trailingWeightTons());
        System.out.printf("Total Tractive Power    : %,d HP%n", metrics.totalHorsepower());
        System.out.printf("Horsepower/Ton Ratio    : %.2f HP/Ton %s%n", metrics.horsepowerPerTonRatio(),
                metrics.horsepowerPerTonRatio() >= 1.5 ? "[Optimal For Mountain Grades]" : "[Standard Freight]");
        System.out.printf("Operative Brake Ratio   : %.1f%% %s%n", metrics.operativeBrakePercentage(),
                metrics.isMeetsBrakeRegulations() ? "[Compliant >= 85%]" : "[VIOLATION < 85%]");
        System.out.printf("Max Allowable Speed     : %.1f km/h%n", metrics.maxAllowableSpeedKmh());
        System.out.printf("Estimated Drawbar Pull  : %,.1f kN (at 1.0%% grade, 50 km/h)%n", metrics.estimatedDrawbarPullKN());

        // Grade hauling capability
        double maxTonnageOnGrade = tractiveCalculator.calculateMaxTrailingTonnageOnGrade(consist, 1.5, 40.0);
        System.out.printf("Max Haulable on 1.5%%    : %,.1f tons at 40 km/h%n", maxTonnageOnGrade);

        // Stopping distance estimation
        System.out.println("\n------------------------- [ STOPPING DISTANCE ANALYSIS ] -------------------------");
        StoppingDistanceEstimator.StoppingResult stopResult = stoppingEstimator.calculateStoppingDistance(consist, 80.0, 0.0);
        System.out.printf("Initial Test Speed      : %.1f km/h%n", stopResult.initialSpeedKmh());
        System.out.printf("Emergency Stopping Dist : %,.1f meters (%.1f seconds)%n",
                stopResult.emergencyStoppingDistanceMeters(), stopResult.emergencyStopDurationSeconds());
        System.out.printf("Full Service Stop Dist  : %,.1f meters%n", stopResult.serviceStoppingDistanceMeters());
        System.out.printf("Deceleration Rate       : %.3f m/s^2%n", stopResult.decelerationRateMpss());
        System.out.printf("Signaling Block Safety  : %s%n",
                stopResult.withinSafeSignalingBlock() ? "SAFE (Within 2500m standard block)" : "EXCEEDED SIGNAL BLOCK - REQUIRES ADVANCE BRAKING");

        // Weight distribution & string-lining analysis
        System.out.println("\n----------------------- [ WEIGHT DISTRIBUTION & STRING-LINING ] ------------------");
        WeightDistributionAnalyzer.DistributionReport distReport = weightAnalyzer.analyze(consist);
        System.out.printf("Weight Spread (Head/Mid/Tail) : %.1f T / %.1f T / %.1f T (%s)%n",
                distReport.headWeightTons(), distReport.midWeightTons(), distReport.tailWeightTons(),
                distReport.isForwardWeighted() ? "Head-End Heavy (Optimal)" : "Tail-End Heavy (Caution)");
        System.out.printf("Center of Gravity Location   : %.1f meters from lead coupler%n", distReport.centerOfGravityPositionMeters());
        System.out.printf("Curving Derailment Risk Score: %.2f / 1.00%n", distReport.overallCurvingRiskScore());

        if (!distReport.detectedAnomalies().isEmpty()) {
            System.out.println("[WARNING] STRING-LINING DERAILEMT RISKS DETECTED:");
            for (WeightDistributionAnalyzer.WeightAnomaly a : distReport.detectedAnomalies()) {
                System.out.printf("  Car #%d (%s): Weight=%.1f T | Trailing=%.1f T | L/V Ratio=%.3f%n    -> %s%n",
                        a.vehicleIndex() + 1, a.vehicleId(), a.carWeightTons(), a.trailingBlockWeightTons(),
                        a.lateralToVerticalRatio(), a.hazardDescription());
            }
        } else {
            System.out.println("[SUCCESS] Weight gradient is smooth. No string-lining risks detected.");
        }
        System.out.println("================================================================================");
    }

    public void runYardClassificationSimulation() {
        System.out.println("\n======================= [ YARD SWITCHING & HUMP SIMULATOR ] =======================");
        System.out.println("Terminal Yard: " + yardEngine.getYardName() + " (" + yardEngine.getYardCode() + ")");

        // Receive consist into arrival track
        yardEngine.receiveConsist(TrainConsistBuilder.buildIntermodalTranscon(), "ARR-01");

        YardTrack inboundTrack = yardEngine.getTrack("ARR-01");
        System.out.println("Inbound Track Status: " + inboundTrack);

        System.out.println("\n>>> Initiating Gravity Hump Sorting down the classification ladder...");
        Map<String, Integer> humpCounts = yardEngine.executeHumpSort("ARR-01");
        for (Map.Entry<String, Integer> entry : humpCounts.entrySet()) {
            YardTrack t = yardEngine.getTrack(entry.getKey());
            System.out.printf("  Track %-8s (%-30s): %d cars routed%n", entry.getKey(), t.getTrackName(), entry.getValue());
        }

        System.out.println("\n>>> Demonstrating Shunting moves using LinkedList LIFO/FIFO operations (UC5)...");
        yardEngine.shuntCar("CLASS-01", true, "DEP-01", true);
        yardEngine.shuntCar("CLASS-04", false, "DEP-01", true);

        System.out.println("\n>>> Demonstrating Bubble Sort algorithm on Track CLASS-01 by car weight (UC16)...");
        yardEngine.sortTrackByWeightBubbleSort("CLASS-01", true);

        System.out.println("\nCurrent Yard Tracks Overview:");
        for (YardTrack t : yardEngine.getAllTracks()) {
            System.out.println("  " + t);
        }
        System.out.println("==================================================================================");
    }

    public void runSearchAndInventoryAnalytics() {
        System.out.println("\n================= [ SEARCH, REGEX & INVENTORY ANALYTICS (UC3-UC20) ] =================");

        // Regex validation (UC11)
        System.out.println("--- 1. Equipment ID & UN Hazmat Code Regex Validation (UC11) ---");
        String[] testIds = {"BNSF-7489", "TTX-554210", "INVALID_CAR_ID", "UP-8842"};
        for (String id : testIds) {
            System.out.printf("  Reporting Mark '%s' Valid: %b%n", id, searchService.isValidEquipmentId(id));
        }

        String[] testHazmat = {"UN1017", "UN1203", "TOXIC-GAS", "UN9999"};
        for (String hz : testHazmat) {
            System.out.printf("  UN Placard Code '%s' Valid: %b%n", hz, searchService.isValidHazmatCode(hz));
        }

        // Set Uniqueness & Natural Ordering (UC3, UC4)
        System.out.println("\n--- 2. Unique Vehicle IDs (HashSet UC3 & TreeSet UC4) ---");
        Set<String> uniqueIds = searchService.getUniqueVehicleIds(activeConsist);
        TreeSet<String> sortedUniqueIds = searchService.getSortedUniqueVehicleIds(activeConsist);
        System.out.println("  HashSet Unique IDs Count : " + uniqueIds.size());
        System.out.println("  TreeSet Ordered Sample   : " + sortedUniqueIds.stream().limit(5).toList() + " ...");

        // Map Capacity (UC6)
        System.out.println("\n--- 3. Equipment Tonnage Lookup Map (UC6) ---");
        Map<String, Double> tonMap = searchService.mapVehicleTonnages(activeConsist);
        System.out.println("  Sample Tonnage Mappings : " + tonMap.entrySet().stream().limit(4).toList());

        // Stream Filter & Grouping (UC8, UC9, UC10)
        System.out.println("\n--- 4. Stream Filter, Grouping & Total Reductions (UC8, UC9, UC10) ---");
        List<RollingStock> heavyCars = searchService.filterHeavyVehicles(activeConsist, 100.0);
        System.out.printf("  Vehicles with Gross Weight >= 100 Tons (UC8): %d units%n", heavyCars.size());

        Map<CarType, List<RollingStock>> grouped = searchService.groupVehiclesByType(activeConsist);
        System.out.println("  Vehicles Grouped by Car Type (UC9):");
        grouped.forEach((type, list) -> System.out.printf("    %-24s: %d units%n", type, list.size()));

        // Search: Linear vs Binary (UC18, UC19, UC20)
        System.out.println("\n--- 5. Linear Search vs Logarithmic Binary Search (UC18, UC19) ---");
        String targetCar = activeConsist.getVehicles().get(activeConsist.getVehicleCount() / 2).getVehicleId();

        // Linear search (UC18)
        Optional<RollingStock> linearResult = searchService.linearSearch(activeConsist, targetCar);
        System.out.printf("  Linear Search for '%s': Found = %b (%s)%n",
                targetCar, linearResult.isPresent(), linearResult.map(RollingStock::getCarType).orElse(null));

        // Binary search (UC19)
        List<RollingStock> sortedList = searchService.sortByVehicleIdAlphabetical(activeConsist);
        RollingStock[] sortedArr = sortedList.toArray(new RollingStock[0]);
        int binaryIdx = searchService.binarySearch(sortedArr, targetCar);
        System.out.printf("  Binary Search for '%s': Found at sorted index #%d%n", targetCar, binaryIdx);

        // Exception Handling Demonstration (UC14, UC15, UC20)
        System.out.println("\n--- 6. Exception Boundary Protections (UC14, UC15, UC20) ---");
        try {
            TrainConsist emptyTrain = new TrainConsist("EMPTY", "EMP", "A", "B");
            searchService.linearSearch(emptyTrain, "TEST");
        } catch (IllegalStateException e) {
            System.out.println("  [PROTECTION UC20] Successfully caught empty consist search exception: " + e.getMessage());
        }

        try {
            if (activeConsist.getVehicleCount() < 1) {
                throw new InvalidEquipmentException("Consist has zero rolling stock!");
            }
            System.out.println("  [PROTECTION UC14] Equipment capacity check passed.");
        } catch (InvalidEquipmentException e) {
            System.out.println("  [PROTECTION UC14] InvalidEquipmentException handled: " + e.getMessage());
        }

        System.out.println("======================================================================================");
    }

    public void runPerformanceBenchmark() {
        System.out.println("\n================= [ LOOP VS STREAM PERFORMANCE BENCHMARK (UC13) ] =================");
        System.out.println("Benchmarking 100,000 data elements filter operation across iterations...");

        PerformanceBenchmarkService.BenchmarkResult result = benchmarkService.runFilterBenchmark(100_000);
        System.out.printf("Sample Size      : %,d elements%n", result.dataSize());
        System.out.printf("Iterative Loop   : %,d ns (%.3f ms)%n", result.iterativeLoopNanos(), result.loopMilliseconds());
        System.out.printf("Stream Pipeline  : %,d ns (%.3f ms)%n", result.streamPipelineNanos(), result.streamMilliseconds());
        System.out.printf("Fastest Method   : %s (%.1fx faster)%n", result.fasterEngine(), result.speedupFactor());
        System.out.println("===================================================================================");
    }

    private void switchActiveConsist(Scanner scanner) {
        System.out.println("\nChoose train preset to load:");
        System.out.println(" [1] BNSF Heavy Intermodal Transcon (Fully Compliant)");
        System.out.println(" [2] CSX Chemical Manifest with Toxic Gas & Buffer Cars (Compliant)");
        System.out.println(" [3] Amtrak Superliner Coast Starlight Express (Passenger)");
        System.out.println(" [4] Hazard Demonstration Train (Violations: String-Lining, No Buffers, Defective Brakes)");
        System.out.print("Select (1-4): ");

        String choice = scanner.hasNextLine() ? scanner.nextLine().trim() : "1";
        switch (choice) {
            case "2" -> {
                activeConsist = TrainConsistBuilder.buildChemicalManifest();
                System.out.println("\n[LOADED] CSX Chemical Manifest train set active.");
            }
            case "3" -> {
                activeConsist = TrainConsistBuilder.buildPassengerExpress();
                System.out.println("\n[LOADED] Amtrak Superliner Express set active.");
            }
            case "4" -> {
                activeConsist = TrainConsistBuilder.buildHazardsDemoTrain();
                System.out.println("\n[LOADED] Hazard Demonstration Train set active.");
            }
            default -> {
                activeConsist = TrainConsistBuilder.buildIntermodalTranscon();
                System.out.println("\n[LOADED] BNSF Heavy Intermodal Transcon set active.");
            }
        }
    }

    /**
     * Executes the comprehensive 20 Use Cases automated showcase.
     */
    public void runAutomatedShowcase() {
        System.out.println("\n==========================================================================================");
        System.out.println("          STARTING COMPREHENSIVE 20 USE CASES ENTERPRISE SUITE SHOWCASE                   ");
        System.out.println("==========================================================================================");

        // UC1: Consist Initialization
        System.out.println("\n--- [UC1] Train Consist Initialization ---");
        TrainConsist uc1Train = new TrainConsist("UC1-EXP", "EXP-01", "Yard A", "Yard B");
        System.out.println("Initial consist vehicle count: " + uc1Train.getVehicleCount());

        // UC2: Insertion & Removal in List
        System.out.println("\n--- [UC2] Vehicle Insertion, Deletion & Membership ---");
        TrainConsist uc2Train = TrainConsistBuilder.buildPassengerExpress();
        System.out.println("Before removal: " + uc2Train.getVehicleCount() + " vehicles");
        RollingStock removed = uc2Train.removeVehicle(2);
        System.out.println("Removed vehicle at index 2: " + removed.getVehicleId());
        System.out.println("Consist vehicle count after removal: " + uc2Train.getVehicleCount());

        // UC3: Unique Bogie IDs with HashSet
        System.out.println("\n--- [UC3] Unique Rolling Stock IDs via HashSet ---");
        Set<String> uc3Set = searchService.getUniqueVehicleIds(uc2Train);
        System.out.println("Unique Vehicle IDs in HashSet: " + uc3Set);

        // UC4: Ordered Unique Bogie IDs with TreeSet
        System.out.println("\n--- [UC4] Sorted Unique IDs via TreeSet ---");
        TreeSet<String> uc4Set = searchService.getSortedUniqueVehicleIds(uc2Train);
        System.out.println("Sorted Unique IDs in TreeSet: " + uc4Set);

        // UC5: Consist Head & Tail Operations with LinkedList
        System.out.println("\n--- [UC5] Head/Tail Shunting with LinkedList ---");
        YardTrack uc5Track = new YardTrack("TRACK-UC5", "Shunting Track", 500.0);
        uc5Track.pushToHead(new Locomotive("LEAD-01", "TEST", "01", 190.0, 22.0, 100.0, 4000, 800.0, Locomotive.TractionType.DIESEL_ELECTRIC, true));
        uc5Track.pushToTail(new FreightCar("CAR-01", "TEST", "01", CarType.BOXCAR, 30.0, 70.0, 40.0, 18.0, 100.0, "Goods", HazmatClass.NONE, null, "Yard B"));
        System.out.println("Track vehicles count after head & tail additions: " + uc5Track.getVehicleCount());
        RollingStock headCar = uc5Track.popFromHead();
        System.out.println("Popped head car: " + headCar.getVehicleId());

        // UC6: Bogie Capacity Mappings via Map
        System.out.println("\n--- [UC6] Equipment Capacity / Tonnage Map ---");
        Map<String, Double> uc6Map = searchService.mapVehicleTonnages(uc2Train);
        uc6Map.forEach((id, wt) -> System.out.println("Vehicle: " + id + " | Weight: " + wt + " tons"));

        // UC7: Bogie Sorting by Comparator
        System.out.println("\n--- [UC7] Sorting Vehicles by Weight via Comparator ---");
        List<RollingStock> uc7Sorted = searchService.sortByGrossWeight(uc2Train);
        for (RollingStock v : uc7Sorted) {
            System.out.printf("  %s (%s) - %.1f tons%n", v.getVehicleId(), v.getCarType(), v.getGrossWeightTons());
        }

        // UC8: Filter High Capacity via Stream
        System.out.println("\n--- [UC8] Filter Vehicles by Tonnage via Stream ---");
        List<RollingStock> uc8Filtered = searchService.filterHeavyVehicles(activeConsist, 100.0);
        System.out.println("Vehicles >= 100 tons count: " + uc8Filtered.size());

        // UC9: Grouping by Category via Stream
        System.out.println("\n--- [UC9] Grouping Rolling Stock by Category via Stream ---");
        Map<CarType, List<RollingStock>> uc9Grouped = searchService.groupVehiclesByType(activeConsist);
        uc9Grouped.forEach((type, list) -> System.out.println("  " + type + ": " + list.size() + " cars"));

        // UC10: Seating Capacity Aggregation via Reduce
        System.out.println("\n--- [UC10] Aggregating Passenger Capacity via Stream Reduce ---");
        TrainConsist passengerTrain = TrainConsistBuilder.buildPassengerExpress();
        int totalSeats = searchService.calculateTotalPassengerCapacity(passengerTrain);
        System.out.println("Total Passenger Seating Capacity on Train: " + totalSeats + " seats");

        // UC11: Regex Validation of Train & Cargo IDs
        System.out.println("\n--- [UC11] Pattern Validation for Equipment Marks & Hazmat ---");
        System.out.println("BNSF-7489 matches pattern: " + searchService.isValidEquipmentId("BNSF-7489"));
        System.out.println("UN1017 matches hazmat pattern: " + searchService.isValidHazmatCode("UN1017"));

        // UC12: Cargo Shape & Type Compatibility
        System.out.println("\n--- [UC12] Equipment Shape & Cargo Safety Compatibility ---");
        boolean chemicalTrainSafe = TrainConsistBuilder.buildChemicalManifest().getFreightCars().stream()
                .allMatch(fc -> fc.getHazmatClass() != HazmatClass.CLASS_2_3_POISON_GAS || fc.getCarType() == CarType.TANK_CAR);
        System.out.println("Chemical train cargo-to-car compatibility compliant: " + chemicalTrainSafe);

        // UC13: Loop vs Stream Performance Benchmark
        System.out.println("\n--- [UC13] Performance Benchmark: Loop vs Stream ---");
        PerformanceBenchmarkService.BenchmarkResult bench = benchmarkService.runFilterBenchmark(100_000);
        System.out.printf("Loop: %.3f ms | Stream: %.3f ms | Faster: %s (%.1fx)%n",
                bench.loopMilliseconds(), bench.streamMilliseconds(), bench.fasterEngine(), bench.speedupFactor());

        // UC14: Custom Checked Exception for Invalid Capacity
        System.out.println("\n--- [UC14] Checked Exception Handling (InvalidEquipmentException) ---");
        try {
            if (activeConsist.getTotalGrossWeightTons() <= 0) {
                throw new InvalidEquipmentException("Train tonnage cannot be zero or negative!");
            }
            System.out.println("Train tonnage validation passed: " + activeConsist.getTotalGrossWeightTons() + " tons");
        } catch (InvalidEquipmentException e) {
            System.out.println("Caught InvalidEquipmentException: " + e.getMessage());
        }

        // UC15: Custom Runtime Exception for Cargo Safety
        System.out.println("\n--- [UC15] Runtime Exception Handling (UnsafeHazmatPlacementException) ---");
        try {
            FreightCar badAssignment = new FreightCar("FLAT-99", "TEST", "99", CarType.FLATCAR, 25.0, 80.0, 75.0, 16.0, 80.0, "Liquid Chlorine", HazmatClass.CLASS_2_3_POISON_GAS, "UN1017", "Yard B");
            if (badAssignment.getCarType() == CarType.FLATCAR && badAssignment.getHazmatClass().isHazardous()) {
                throw new UnsafeHazmatPlacementException("Hazardous toxic materials cannot be loaded onto an open flatcar!");
            }
        } catch (UnsafeHazmatPlacementException e) {
            System.out.println("Caught UnsafeHazmatPlacementException: " + e.getMessage());
        } finally {
            System.out.println("Cargo safety audit checkpoint logged.");
        }

        // UC16: Bubble Sort Algorithm Implementation
        System.out.println("\n--- [UC16] In-Place Bubble Sort by Gross Weight ---");
        YardTrack bubbleTrack = new YardTrack("BUBBLE-01", "Sort Track", 1000.0);
        bubbleTrack.pushToTail(new FreightCar("CAR-3", "TEST", "3", CarType.BOXCAR, 30.0, 90.0, 80.0, 16.0, 90.0, "Paper", HazmatClass.NONE, null, "Yard B"));
        bubbleTrack.pushToTail(new FreightCar("CAR-1", "TEST", "1", CarType.BOXCAR, 30.0, 90.0, 5.0, 16.0, 90.0, "Empty", HazmatClass.NONE, null, "Yard B"));
        bubbleTrack.pushToTail(new FreightCar("CAR-2", "TEST", "2", CarType.BOXCAR, 30.0, 90.0, 55.0, 16.0, 90.0, "Lumber", HazmatClass.NONE, null, "Yard B"));

        yardEngine.addTrack(bubbleTrack);
        yardEngine.sortTrackByWeightBubbleSort("BUBBLE-01", false);
        for (RollingStock v : bubbleTrack.getVehicles()) {
            System.out.println("  Sorted car: " + v.getVehicleId() + " -> " + v.getGrossWeightTons() + " tons");
        }

        // UC17: Alphabetical Sorting by Vehicle ID
        System.out.println("\n--- [UC17] Alphabetical Sort by Vehicle ID ---");
        List<RollingStock> alphaSorted = searchService.sortByVehicleIdAlphabetical(activeConsist);
        System.out.println("First 4 alphabetically sorted cars: " + alphaSorted.stream().limit(4).map(RollingStock::getVehicleId).toList());

        // UC18: Linear Search
        System.out.println("\n--- [UC18] Linear Search by Vehicle ID ---");
        String testVehicleId = activeConsist.getVehicles().get(0).getVehicleId();
        Optional<RollingStock> foundLinear = searchService.linearSearch(activeConsist, testVehicleId);
        System.out.printf("Linear search for '%s': Found = %b%n", testVehicleId, foundLinear.isPresent());

        // UC19: Binary Search
        System.out.println("\n--- [UC19] Logarithmic Binary Search ---");
        RollingStock[] sortedVehicles = alphaSorted.toArray(new RollingStock[0]);
        int binResult = searchService.binarySearch(sortedVehicles, testVehicleId);
        System.out.printf("Binary search for '%s': Found at array index %d%n", testVehicleId, binResult);

        // UC20: Empty Consist Validation & Exception
        System.out.println("\n--- [UC20] Empty Consist Search Validation ---");
        TrainConsist emptyTrain = new TrainConsist("EMPTY-TRAIN", "EMP", "A", "B");
        try {
            searchService.linearSearch(emptyTrain, "B101");
        } catch (IllegalStateException e) {
            System.out.println("Caught IllegalStateException on empty train: " + e.getMessage());
        }

        System.out.println("\n==========================================================================================");
        System.out.println("            ALL 20 USE CASES SUCCESSFULLY VALIDATED AND DEMONSTRATED!                     ");
        System.out.println("==========================================================================================");
    }
}
