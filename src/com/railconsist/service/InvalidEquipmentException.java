package com.railconsist.service;

/**
 * Checked exception thrown when rolling stock attributes or capacities violate safety boundaries (UC14).
 */
public class InvalidEquipmentException extends Exception {
    public InvalidEquipmentException(String message) {
        super(message);
    }

    public InvalidEquipmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
