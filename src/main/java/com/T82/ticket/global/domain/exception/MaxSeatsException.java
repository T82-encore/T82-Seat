package com.T82.ticket.global.domain.exception;

public class MaxSeatsException extends IllegalArgumentException {
    public MaxSeatsException(String message) {
        super(message);
    }
}
