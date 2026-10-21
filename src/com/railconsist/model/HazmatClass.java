package com.railconsist.model;

/**
 * Hazardous material classification compliant with US DOT 49 CFR and UN Hazard Codes.
 * Defines required isolation and buffer car distances in train consists.
 */
public enum HazmatClass {
    NONE("Non-Hazardous", "NON", 0, false),
    CLASS_1_EXPLOSIVES("Class 1: Mass Detonation Explosives", "EXP-1.1", 5, true),
    CLASS_2_1_FLAMMABLE_GAS("Class 2.1: Flammable Gas (Propane / LPG)", "FLAM-GAS", 5, true),
    CLASS_2_2_NON_FLAMMABLE_GAS("Class 2.2: Non-Flammable Compressed Gas", "COMP-GAS", 1, false),
    CLASS_2_3_POISON_GAS("Class 2.3: Toxic Inhalation Hazard (TIH/PIH Chlorine)", "TIH-GAS", 5, true),
    CLASS_3_FLAMMABLE_LIQUIDS("Class 3: Flammable Liquids (Crude Oil / Ethanol)", "FLAM-LIQ", 5, true),
    CLASS_4_FLAMMABLE_SOLIDS("Class 4: Flammable Solids", "FLAM-SOL", 3, true),
    CLASS_5_OXIDIZERS("Class 5: Oxidizers & Organic Peroxides", "OXIDIZER", 3, true),
    CLASS_6_1_TOXIC("Class 6.1: Toxic & Poisonous Substances", "TOXIC", 3, true),
    CLASS_7_RADIOACTIVE("Class 7: Radioactive Materials", "RADIO", 5, true),
    CLASS_8_CORROSIVE("Class 8: Corrosive Liquids/Acids", "CORROSIVE", 2, true),
    CLASS_9_MISC_DANGEROUS("Class 9: Miscellaneous Dangerous Goods", "MISC-DG", 1, false);

    private final String description;
    private final String placardCode;
    private final int requiredBufferCars;
    private final boolean highRisk;

    HazmatClass(String description, String placardCode, int requiredBufferCars, boolean highRisk) {
        this.description = description;
        this.placardCode = placardCode;
        this.requiredBufferCars = requiredBufferCars;
        this.highRisk = highRisk;
    }

    public String getDescription() {
        return description;
    }

    public String getPlacardCode() {
        return placardCode;
    }

    public int getRequiredBufferCars() {
        return requiredBufferCars;
    }

    public boolean isHighRisk() {
        return highRisk;
    }

    public boolean isHazardous() {
        return this != NONE;
    }
}
