package com.railconsist.model;

/**
 * Standard Association of American Railroads (AAR) and UIC rolling stock categories.
 */
public enum CarType {
    LOCOMOTIVE("Locomotive Power Unit", true, false),
    BOXCAR("General Freight Boxcar", false, true),
    TANK_CAR("Liquid Bulk Tank Car", false, true),
    COVERED_HOPPER("Covered Grain & Mineral Hopper", false, true),
    OPEN_HOPPER("Open Top Aggregate Hopper", false, true),
    FLATCAR("Heavy Bulk Flatcar", false, true),
    REFRIGERATED("Refrigerated Cargo Car", false, true),
    INTERMODAL_WELL("Double-Stack Intermodal Well Car", false, true),
    AUTORACK("Multi-Level Automotive Carrier", false, true),
    PASSENGER_COACH("Passenger Transit Coach", false, false),
    CABOOSE("Manned Crew Caboose", false, false),
    END_OF_TRAIN_DEVICE("Telemetry Telematics Unit (EOTD / FRED)", false, false);

    private final String description;
    private final boolean locomotive;
    private final boolean freight;

    CarType(String description, boolean locomotive, boolean freight) {
        this.description = description;
        this.locomotive = locomotive;
        this.freight = freight;
    }

    public String getDescription() {
        return description;
    }

    public boolean isLocomotive() {
        return locomotive;
    }

    public boolean isFreight() {
        return freight;
    }
}
