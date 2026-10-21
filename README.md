# RailConsist Enterprise

RailConsist Enterprise is an enterprise-grade railway consist management, train dynamics physics simulator, classification yard switching manager, and regulatory marshalling compliance audit platform built with Java 21 LTS.

The system scales foundational core Java data structures, algorithms, exception semantics, and functional stream pipelines into an Association of American Railroads (AAR) and Federal Railroad Administration (FRA) compliant rail simulation engine.

---

## Architectural Overview

RailConsist Enterprise is architected around Domain-Driven Design (DDD) principles:

- **Domain Model (`com.railconsist.model`)**: Object-oriented rolling stock hierarchy modeling physical mechanical parameters, couplers, brake systems, hazardous material placards, and consist aggregates.
- **Physics Dynamics Engine (`com.railconsist.physics`)**: Numerical models calculating train tractive effort, Davis equation train resistance, grade resistance, emergency braking stopping distances, and string-lining derailment risks.
- **Marshalling Safety Rule Engine (`com.railconsist.rules`)**: Extensible rule evaluation framework validating consist safety against FRA 49 CFR regulations and AAR standard operating procedures.
- **Yard Management & Classification (`com.railconsist.yard`)**: Flat and gravity hump classification yard simulator utilizing double-ended linked lists, LIFO/FIFO shunting tracks, and sorting algorithms.
- **Service & Inventory Infrastructure (`com.railconsist.service`)**: High-performance linear and logarithmic binary search lookups, AAR reporting mark regex validators, custom checked/runtime exception boundaries, stream aggregations, and nanosecond micro-benchmarking.
- **Application & CLI Entry Point (`com.railconsist`)**: Interactive console terminal interface and automated batch execution suite.

---

## Technical Specifications

- **Language Level**: Java 21 LTS
- **Build Tooling**: Maven 3.9+ (`pom.xml`) and Gradle 8+ (`build.gradle`)
- **Third-Party Runtime Dependencies**: None (Standard Java 21 SE runtime only)
- **Concurrency**: Thread-safe synchronized operations across Consist and YardTrack aggregates
- **Testing**: Dedicated 23-scenario automated integration test suite (`TrainConsistSystemTest`)

---

## Domain Model Architecture

```
                       +-----------------------+
                       |     RollingStock      |  (Abstract Base)
                       +-----------------------+
                       | - vehicleId: String   |
                       | - grossWeightTons     |
                       | - lengthMeters        |
                       | - brakeStatus         |
                       | - couplerType         |
                       +-----------+-----------+
                                   |
         +-----------------+-------+---------+-------------------+
         |                 |                 |                   |
+--------+------+  +-------+-------+  +------+--------+  +-------+-------+
|  Locomotive   |  |   FreightCar  |  | PassengerCoach|  |EndOfTrainDevice|
+---------------+  +---------------+  +---------------+  +---------------+
| horsepower    |  | cargoCommodity|  |seatingCapacity|  | batteryPercent|
| tractiveEffort|  | hazmatClass   |  |coachClass     |  | markerLight   |
| tractionType  |  | unNumber      |  |passengerCount |  | radioTelemetry|
| isLeadUnit    |  | maxCapacity   |  +---------------+  | brakePipePsi  |
+---------------+  +---------------+                     +---------------+
```

### Enumerations and Value Objects

- **`CarType`**: Categorizes equipment into LOCOMOTIVE, BOXCAR, TANK_CAR, COVERED_HOPPER, OPEN_HOPPER, FLATCAR, REFRIGERATED, INTERMODAL_WELL, AUTORACK, PASSENGER_COACH, CABOOSE, and END_OF_TRAIN_DEVICE.
- **`BrakeStatus`**: OPERATIONAL (100% efficiency), REDUCED_EFFICIENCY (75%), CUT_OUT (0%), and DEFECTIVE (0%).
- **`CouplerType`**: AAR_TYPE_E (1800 kN tensile limit), AAR_TYPE_F (2200 kN interlocking), ROTARY_DUMP (2000 kN), and TIGHTLOCK (2500 kN passenger standard).
- **`HazmatClass`**: Models Title 49 CFR Hazardous Materials regulations including explosive classifications, poisonous gases (TIH/PIH), flammable liquids, corrosives, oxidizers, and radioactive substances with mandatory isolation distance metrics.

---

## Train Physics and Dynamics Calculations

The physics engine implements empirical formulas standard in North American and international railway engineering:

### 1. Modified Davis Equation (Train Resistance)

$$R_{total} = R_{davis} + R_{grade} + R_{curve}$$

Where:
- $R_{davis} = (A + B \cdot v) \cdot W + C \cdot v^2$
  - $A = 1.3 \text{ lbs/ton}$ (Journal bearing and rolling friction)
  - $B = 0.03 \text{ lbs/ton/mph}$ (Flange friction)
  - $C = 0.0024 \text{ drag coefficient}$ (Aerodynamic drag)
  - $v = \text{Velocity in mph}$
  - $W = \text{Train gross weight in tons}$
- $R_{grade} = 20 \text{ lbs/ton per } 1\% \text{ grade} \cdot W$
- $R_{curve} = 0.8 \text{ lbs/ton per degree of curvature} \cdot W$
- Unit conversion factors transform output into kiloNewtons (kN).

### 2. Tractive Effort and Mountain Grade Hauling Capacity

Determines the maximum trailing tonnage a locomotive consist can haul over a ruling grade without exceeding continuous tractive ratings or causing wheelslip:

$$\text{Max Trailing Tonnage} = \frac{\text{Total Available Tractive Effort} - \text{Locomotive Grade Resistance}}{\text{Unit Trailing Resistance on Grade}}$$

### 3. Emergency Stopping Distance Estimator

Calculates the distance required to bring a train to a complete stop:

$$d = (v \cdot t_{delay}) + \frac{v^2}{2 \cdot a_{decel}}$$

- Incorporates pneumatic air brake propagation delay ($~280 \text{ m/s}$ along the train brake pipe).
- Dynamically adjusts deceleration based on operative brake percentages, nominal brake shoe friction coefficients, and track gradients.
- Evaluates stopping performance against standard signaling block spacing (2,500 meters).

### 4. String-Lining Derailment Analyzer

String-lining occurs when high draft forces (pulling tension) on a curve act on light cars positioned ahead of a long block of heavy trailing tonnage, generating an inward lateral force ($L$) that overcomes the vertical wheel load ($V$).
- The analyzer evaluates Lateral-to-Vertical ($L/V$) force ratios and flags hazardous configurations where empty cars ($< 35 \text{ tons}$) precede heavy blocks ($> 3,500 \text{ tons}$).

---

## Safety and Regulatory Marshalling Engine

The `MarshallingRuleEngine` audits consists against Federal Railroad Administration (FRA) standards:

1. **Hazmat Buffer Separation (FRA 49 CFR Section 174.85)**:
   - Placarded hazardous tank cars (e.g., Toxic Inhalation Hazard / Chlorine UN1017) must be buffered by non-hazardous cars from operating locomotives, passenger cars, or cabooses.
   - Evaluates minimum 5-car buffer separation on line-haul trains and at least 1 buffer car on short consists.
2. **Operative Brake Percentage Compliance (FRA 49 CFR Section 232)**:
   - Mandatory minimum of 85.0% operative brakes throughout the consist.
   - Halts dispatch if defective or cut-out brakes exceed 15.0% of total consist weight.
3. **Rear-End Telemetry Compliance (FRA 49 CFR Section 232.403)**:
   - Verifies the trailing car is either a manned caboose or an operational End-of-Train Device (EOTD/FRED) reporting brake pipe pressure and functioning marker lights.
4. **String-Lining Prevention**:
   - Detects severe weight imbalances and wheel-climb derailment vectors on curves.
5. **Maximum Train Length and Tonnage Limits**:
   - Enforces maximum structural thresholds (12,000 tons and 3,600 meters) to avoid coupler knuckle failure and draft gear overload.

---

## Classification Yard Simulation

The `YardClassificationEngine` and `YardTrack` manage yard switching operations:

- **Receiving & Inbound Tracks**: Accepts inbound consists for processing.
- **Gravity Hump Classification**: Simulates gravity humping where uncoupled cars roll down a switching ladder into dedicated classification tracks based on equipment type, hazardous placards, and block priority.
- **Shunting Moves**: Head-end and tail-end push/pop operations using Java `LinkedList` for FIFO/LIFO track management.
- **Track Capacity Tracking**: Enforces physical track meter capacities and gross tonnage limits.
- **Sorting Algorithms**: In-place Bubble Sort and Comparator-driven sorting algorithms organizing classification tracks by weight gradient prior to outbound departure makeup.

---

## Coverage of 20 Core Use Cases (UC1 to UC20)

RailConsist Enterprise systematically integrates all 20 curriculum use cases:

| Use Case | Core Requirement | Enterprise Implementation |
| :--- | :--- | :--- |
| **UC1** | Consist Initialization | `TrainConsist` aggregate creation with symbol, route, and empty vehicle list. |
| **UC2** | List Insertion, Removal & Queries | Dynamic vehicle insertion, positional index deletion, and presence checks. |
| **UC3** | Unique ID Filtering via HashSet | `ConsistSearchAndInventoryService.getUniqueVehicleIds()` using `HashSet<String>`. |
| **UC4** | Natural Sorted Order via TreeSet | `ConsistSearchAndInventoryService.getSortedUniqueVehicleIds()` using `TreeSet<String>`. |
| **UC5** | Head/Tail Consist Operations | `YardTrack` LIFO/FIFO shunting moves using `LinkedList.addFirst/Last` and `removeFirst/Last`. |
| **UC6** | Equipment Capacity Map | `ConsistSearchAndInventoryService.mapVehicleTonnages()` using `HashMap<String, Double>`. |
| **UC7** | Sorting by Capacity / Weight | `ConsistSearchAndInventoryService.sortByGrossWeight()` using `Comparator.comparingDouble`. |
| **UC8** | Filtering via Java Streams | `ConsistSearchAndInventoryService.filterHeavyVehicles()` using `stream().filter().collect()`. |
| **UC9** | Grouping Categories via Streams | `ConsistSearchAndInventoryService.groupVehiclesByType()` using `Collectors.groupingBy()`. |
| **UC10** | Aggregations via Stream Reduce | `ConsistSearchAndInventoryService.calculateTotalPassengerCapacity()` using `map().reduce()`. |
| **UC11** | Regex Identifier Pattern Matching | Validating reporting marks (`^[A-Z]{2,5}-\\d{3,6}$`) and UN hazmat codes (`^UN\\d{4}$`). |
| **UC12** | Cargo Shape Compatibility | Validating tank car geometry requirements for hazardous liquids and toxic gases. |
| **UC13** | Loop vs Stream Performance Profiler | `PerformanceBenchmarkService` benchmarking traditional loops against streams across 100,000 items. |
| **UC14** | Custom Checked Exception | `InvalidEquipmentException` thrown when rolling stock attributes violate bounds. |
| **UC15** | Custom Runtime Exception | `UnsafeHazmatPlacementException` thrown during invalid hazardous loading. |
| **UC16** | Classic In-Place Bubble Sort | `YardClassificationEngine.sortTrackByWeightBubbleSort()` algorithm implementation. |
| **UC17** | Alphabetical String Sorting | `ConsistSearchAndInventoryService.sortByVehicleIdAlphabetical()` by vehicle reporting mark. |
| **UC18** | Linear Search ($O(n)$) | `ConsistSearchAndInventoryService.linearSearch()` scanning unordered consists. |
| **UC19** | Binary Search ($O(\log n)$) | `ConsistSearchAndInventoryService.binarySearch()` over sorted equipment arrays. |
| **UC20** | Empty Consist Exception Handling | Input validation throwing `IllegalStateException` on empty search inputs. |

---

## Project Structure

```
c:\Users\Dudekula arshad\OneDrive\Desktop\BLACK
|-- .gitignore
|-- .idea/
|   |-- misc.xml
|   |-- modules.xml
|   |-- vcs.xml
|   `-- runConfigurations/
|       |-- RailConsistEnterprise.xml
|       |-- TrainConsistApp.xml
|       |-- TrainConsistSystemTest.xml
|       `-- UC_Showcase__Batch_.xml
|-- build.gradle
|-- pom.xml
|-- settings.gradle
|-- RailConsistEnterprise.iml
|-- README.md
`-- src/
    |-- TrainConsistApp.java (Top-level convenience entry point)
    `-- com/
        `-- railconsist/
            |-- TrainConsistApplication.java (Main CLI application & batch runner)
            |-- TrainConsistSystemTest.java (23-scenario integration test suite)
            |-- model/
            |   |-- BrakeStatus.java
            |   |-- CarType.java
            |   |-- CouplerType.java
            |   |-- EndOfTrainDevice.java
            |   |-- FreightCar.java
            |   |-- HazmatClass.java
            |   |-- Locomotive.java
            |   |-- PassengerCoach.java
            |   |-- RollingStock.java
            |   `-- TrainConsist.java
            |-- physics/
            |   |-- ConsistMetrics.java
            |   |-- StoppingDistanceEstimator.java
            |   |-- TractiveEffortCalculator.java
            |   `-- WeightDistributionAnalyzer.java
            |-- rules/
            |   |-- BrakingPercentageRule.java
            |   |-- ConsistRule.java
            |   |-- EotPlacementRule.java
            |   |-- HazmatBufferRule.java
            |   |-- MarshallingRuleEngine.java
            |   |-- MaxTrainLengthAndTonnageRule.java
            |   |-- RuleViolation.java
            |   |-- SafetyAuditReport.java
            |   |-- SafetySeverity.java
            |   `-- StringLiningPreventionRule.java
            |-- service/
            |   |-- ConsistSearchAndInventoryService.java
            |   |-- InvalidEquipmentException.java
            |   |-- PerformanceBenchmarkService.java
            |   |-- TrainConsistBuilder.java
            |   `-- UnsafeHazmatPlacementException.java
            `-- yard/
                |-- YardClassificationEngine.java
                `-- YardTrack.java
```

---

## Building and Running

### Prerequisites

- Java Development Kit (JDK) 21 or higher installed and available on `PATH`.
- Check your Java version:
  ```powershell
  java -version
  javac -version
  ```

### Compilation

Compile all source files directly to the output directory:

```powershell
javac -d out/production -sourcepath src src/com/railconsist/model/*.java src/com/railconsist/physics/*.java src/com/railconsist/rules/*.java src/com/railconsist/yard/*.java src/com/railconsist/service/*.java src/com/railconsist/*.java src/*.java
```

### Execution Options

#### 1. Automated 20 Use Cases Batch Showcase
Runs the complete automated demonstration covering Use Cases 1 through 20:

```powershell
java -cp out/production com.railconsist.TrainConsistApplication --batch
```

#### 2. Automated Integration Test Suite
Executes all 23 unit and integration test assertions:

```powershell
java -cp out/production com.railconsist.TrainConsistSystemTest
```

Expected output:
```
========================================================================
     RUNNING RAILCONSIST ENTERPRISE AUTOMATED INTEGRATION TESTS         
========================================================================
  [PASS] Intermodal vehicle count check
  [PASS] Consist horsepower calculation
  [PASS] Consist operative brake compliance
  [PASS] Locomotive count check
  [PASS] Chemical manifest hazmat buffer compliance
  [PASS] Hazmat violation detection on hazard train
  [PASS] Defective brake percentage detection
  [PASS] String-lining risk anomaly detected
  [PASS] High curvature risk score
  [PASS] Missing EOT device detection
  [PASS] Inbound track vehicle loading
  [PASS] Inbound track cleared after hump sort
  [PASS] Bubble sort ascending order verification
  [PASS] Valid equipment mark UP-8842
  [PASS] Valid equipment mark BNSF-7489
  [PASS] Invalid equipment mark with symbols
  [PASS] Valid UN Hazmat UN1017
  [PASS] Invalid UN Hazmat code
  [PASS] Linear search found existing lead unit
  [PASS] Linear search non-existent returns empty
  [PASS] Binary search found correct element
  [PASS] Binary search missing element returns -1
  [PASS] Empty train search throws IllegalStateException (UC20)
========================================================================
TEST SUITE RESULTS: 23/23 PASSED (100% SUCCESS)
========================================================================
```

#### 3. Interactive Terminal Console
Launches the full interactive terminal menu allowing consist switching, live physics calculations, yard hump sorting, and FRA compliance audits:

```powershell
java -cp out/production com.railconsist.TrainConsistApplication
```

#### 4. Running the Standalone Executable JAR
Package and execute the standalone JAR file:

```powershell
jar -cvfe out/RailConsistEnterprise-2.0.0.jar com.railconsist.TrainConsistApplication -C out/production .
java -jar out/RailConsistEnterprise-2.0.0.jar --batch
```

#### 5. IntelliJ IDEA Execution
The repository includes pre-configured run configurations in `.idea/runConfigurations/`:
- Select **`RailConsistEnterprise`**, **`UC Showcase (Batch)`**, or **`TrainConsistSystemTest`** from the run configuration dropdown in the top-right toolbar.
- Click the **Run** button or press `Shift + F10`.

---

## License

This project is licensed under the Apache License, Version 2.0.
