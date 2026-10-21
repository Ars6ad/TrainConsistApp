package com.railconsist.service;

/**
 * Runtime exception thrown during yard or marshalling operations when dangerous cargo is placed unsafely (UC15).
 */
public class UnsafeHazmatPlacementException extends RuntimeException {
    public UnsafeHazmatPlacementException(String message) {
        super(message);
    }

    public UnsafeHazmatPlacementException(String message, Throwable cause) {
        super(message, cause);
    }
}
