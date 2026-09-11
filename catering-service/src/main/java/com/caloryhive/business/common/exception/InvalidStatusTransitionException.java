package com.caloryhive.business.common.exception;

public class InvalidStatusTransitionException extends IllegalStateException {
    public InvalidStatusTransitionException(String fromStatus, String toStatus) {
        super("Invalid status transition from " + fromStatus + " to " + toStatus);
    }

    public InvalidStatusTransitionException(String message) {
        super(message);
    }
}
