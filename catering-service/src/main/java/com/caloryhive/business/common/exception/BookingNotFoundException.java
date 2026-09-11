package com.caloryhive.business.common.exception;

import java.util.UUID;

public class BookingNotFoundException extends ResourceNotFoundException {
    public BookingNotFoundException(UUID id) {
        super("Booking not found: " + id);
    }

    public BookingNotFoundException(String message) {
        super(message);
    }
}
