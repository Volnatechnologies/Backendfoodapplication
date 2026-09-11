package com.caloryhive.business.common.exception;

public class InvalidBookingStatusException extends IllegalStateException {
    public InvalidBookingStatusException(String message) {
        super(message);
    }
}
