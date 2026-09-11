package com.caloryhive.business.common.exception;

import java.util.UUID;

public class CustomOptionNotFoundException extends ResourceNotFoundException {
    public CustomOptionNotFoundException(UUID id) {
        super("Custom option not found: " + id);
    }

    public CustomOptionNotFoundException(String message) {
        super(message);
    }
}
