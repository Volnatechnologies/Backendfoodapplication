package com.caloryhive.business.common.exception;

public class InvalidPricingException extends IllegalArgumentException {
    public InvalidPricingException(String message) {
        super(message);
    }
}
