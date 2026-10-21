package com.railconsist.service;

import com.railconsist.model.*;

/**
 * Factory and builder generating standard North American and European railway train consists.
 */
public class TrainConsistBuilder {

    /**
     * Heavy Intermodal Transcon Freight Train (Compliant, high horsepower-to-tonnage).
     */
    public static TrainConsist buildIntermodalTranscon() {
        TrainConsist consist = new TrainConsist("BNSF-7489", "Q-CHILAC", "Chicago Corwith Yard", "Los Angeles Hobart");

        // Lead power: 2x GE ES44AC
        consist.addVehicle(new Locomotive("BNSF-7489", "BNSF", "7489", 195.0, 22.3, 115.0,
                4400, 880.0, Locomotive.TractionType.DIESEL_ELECTRIC, true));
        consist.addVehicle(new Locomotive("BNSF-7512", "BNSF", "7512", 195.0, 22.3, 115.0,
                4400, 880.0, Locomotive.TractionType.DIESEL_ELECTRIC, false));

        // 8x Double Stack Well Cars (loaded with 53ft domestic containers)
        for (int i = 1; i <= 8; i++) {
            consist.addVehicle(new FreightCar("TTX-" + (550000 + i), "TTX", String.valueOf(550000 + i),
                    CarType.INTERMODAL_WELL, 32.0, 80.0, 75.0, 30.5, 115.0,
                    "Domestic Intermodal 53ft Containers", HazmatClass.NONE, null, "Los Angeles"));
        }

        // Mid-train Distributed Power Unit (DPU)
        consist.addVehicle(new Locomotive("BNSF-7601", "BNSF", "7601", 195.0, 22.3, 115.0,
                4400, 880.0, Locomotive.TractionType.DIESEL_ELECTRIC, false));

        // 6x Additional Double Stacks
        for (int i = 9; i <= 14; i++) {
            consist.addVehicle(new FreightCar("TTX-" + (550000 + i), "TTX", String.valueOf(550000 + i),
                    CarType.INTERMODAL_WELL, 32.0, 80.0, 72.0, 28.0, 115.0,
                    "International 40ft Marine Containers", HazmatClass.NONE, null, "Los Angeles"));
        }

        // End of Train Telemetry Device
        consist.addVehicle(new EndOfTrainDevice("EOT-9021", "BNSF", "EOT-9021"));

        return consist;
    }

    /**
     * Chemical Manifest Freight Train with Hazmat Tank Cars and Required Buffer Cars.
     */
    public static TrainConsist buildChemicalManifest() {
        TrainConsist consist = new TrainConsist("CSX-3104", "M-410", "Waycross Yard, GA", "Philadelphia, PA");

        // Lead Power: 2x EMD SD70ACe
        consist.addVehicle(new Locomotive("CSX-3104", "CSX", "3104", 192.0, 22.5, 110.0,
                4300, 850.0, Locomotive.TractionType.DIESEL_ELECTRIC, true));
        consist.addVehicle(new Locomotive("CSX-3118", "CSX", "3118", 192.0, 22.5, 110.0,
                4300, 850.0, Locomotive.TractionType.DIESEL_ELECTRIC, false));

        // 5x Non-hazmat Buffer Boxcars (Safety requirement ahead of Placarded Hazmat)
        for (int i = 1; i <= 5; i++) {
            consist.addVehicle(new FreightCar("CSX-BOX-" + (100 + i), "CSX", String.valueOf(100 + i),
                    CarType.BOXCAR, 30.0, 70.0, 35.0, 18.3, 105.0,
                    "Lumber & Paper Rolls", HazmatClass.NONE, null, "Philadelphia"));
        }

        // 4x Pressurized Tank Cars (Hazmat Class 2.3 Toxic Gas - Chlorine UN1017)
        for (int i = 1; i <= 4; i++) {
            consist.addVehicle(new FreightCar("GATX-" + (3000 + i), "GATX", String.valueOf(3000 + i),
                    CarType.TANK_CAR, 38.0, 92.0, 90.0, 16.5, 80.0,
                    "Liquid Chlorine", HazmatClass.CLASS_2_3_POISON_GAS, "UN1017", "Philadelphia"));
        }

        // 5x Rear Buffer Covered Hoppers
        for (int i = 1; i <= 5; i++) {
            consist.addVehicle(new FreightCar("CSX-HOP-" + (200 + i), "CSX", String.valueOf(200 + i),
                    CarType.COVERED_HOPPER, 30.0, 85.0, 80.0, 17.5, 105.0,
                    "Grain & Wheat", HazmatClass.NONE, null, "Philadelphia"));
        }

        // Rear EOT
        consist.addVehicle(new EndOfTrainDevice("EOT-4412", "CSX", "EOT-4412"));

        return consist;
    }

    /**
     * Passenger Express Train (Amtrak Style Superliner).
     */
    public static TrainConsist buildPassengerExpress() {
        TrainConsist consist = new TrainConsist("AMTK-156", "TRAIN-14", "Los Angeles Union", "Seattle King Street");

        // 2x Siemens Charger ALC-42
        consist.addVehicle(new Locomotive("AMTK-156", "AMTK", "156", 125.0, 21.8, 200.0,
                4200, 290.0, Locomotive.TractionType.DIESEL_ELECTRIC, true));
        consist.addVehicle(new Locomotive("AMTK-162", "AMTK", "162", 125.0, 21.8, 200.0,
                4200, 290.0, Locomotive.TractionType.DIESEL_ELECTRIC, false));

        // Passenger Coaches
        consist.addVehicle(new PassengerCoach("AMTK-31010", "AMTK", "31010", 65.0, 25.9, 160.0,
                44, 40, PassengerCoach.CoachClass.SLEEPER));
        consist.addVehicle(new PassengerCoach("AMTK-31025", "AMTK", "31025", 65.0, 25.9, 160.0,
                44, 38, PassengerCoach.CoachClass.SLEEPER));
        consist.addVehicle(new PassengerCoach("AMTK-38012", "AMTK", "38012", 62.0, 25.9, 160.0,
                72, 50, PassengerCoach.CoachClass.DINER));
        consist.addVehicle(new PassengerCoach("AMTK-33005", "AMTK", "33005", 60.0, 25.9, 160.0,
                58, 45, PassengerCoach.CoachClass.OBSERVATION));
        consist.addVehicle(new PassengerCoach("AMTK-34080", "AMTK", "34080", 61.0, 25.9, 160.0,
                74, 68, PassengerCoach.CoachClass.COACH));
        consist.addVehicle(new PassengerCoach("AMTK-34092", "AMTK", "34092", 61.0, 25.9, 160.0,
                74, 70, PassengerCoach.CoachClass.COACH));

        return consist;
    }

    /**
     * Train deliberately engineered with regulatory and physics safety hazards
     * to test and demonstrate the MarshallingRuleEngine and WeightDistributionAnalyzer.
     */
    public static TrainConsist buildHazardsDemoTrain() {
        TrainConsist consist = new TrainConsist("DEMO-HAZARD-99", "TEST-FAIL", "Test Yard A", "Test Siding B");

        // Single lead loco
        consist.addVehicle(new Locomotive("DEMO-1001", "DEMO", "1001", 195.0, 22.3, 105.0,
                4400, 850.0, Locomotive.TractionType.DIESEL_ELECTRIC, true));

        // VIOLATION 1: Hazardous Tank Car coupled directly to locomotive with ZERO buffer cars!
        consist.addVehicle(new FreightCar("UNSAFE-CHEM-01", "UNSF", "01",
                CarType.TANK_CAR, 35.0, 95.0, 90.0, 16.5, 75.0,
                "Liquid Chlorine", HazmatClass.CLASS_2_3_POISON_GAS, "UN1017", "Test Dest"));

        // VIOLATION 2: Ultra-light empty flat car right behind hazmat
        consist.addVehicle(new FreightCar("LIGHT-FLAT-02", "LGHT", "02",
                CarType.FLATCAR, 22.0, 80.0, 0.0, 19.8, 105.0,
                "EMPTY", HazmatClass.NONE, null, "Test Dest"));

        // VIOLATION 3: 35 ultra-heavy coal cars trailing behind that empty flat car (String-lining derailment risk!)
        for (int i = 1; i <= 35; i++) {
            FreightCar coalCar = new FreightCar("HEAVY-COAL-" + (100 + i), "COAL", String.valueOf(100 + i),
                    CarType.OPEN_HOPPER, 25.0, 110.0, 105.0, 16.2, 80.0,
                    "Raw Bituminous Coal", HazmatClass.NONE, null, "Test Dest");

            // Some with defective brakes to trigger Brake Percentage violation
            if (i % 3 == 0) {
                coalCar.setBrakeStatus(BrakeStatus.DEFECTIVE);
            }
            consist.addVehicle(coalCar);
        }

        // VIOLATION 4: Missing EOT device at rear! (Last car is a freight car)

        return consist;
    }
}
