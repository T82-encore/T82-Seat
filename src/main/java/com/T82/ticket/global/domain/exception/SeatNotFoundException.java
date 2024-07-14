package com.T82.ticket.global.domain.exception;

public class SeatNotFoundException extends IllegalArgumentException{
    public SeatNotFoundException() {
        super("Not Fount Seat");
    }
}
