package com.caloryhive.business.common.exception;

import java.util.UUID;

public class PackageNotFoundException extends ResourceNotFoundException {
    public PackageNotFoundException(UUID id) {
        super("Package not found: " + id);
    }

    public PackageNotFoundException(String message) {
        super(message);
    }
}
