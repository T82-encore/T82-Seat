package com.T82.ticket.global.domain.exception;

public class EventNotFoundException extends IllegalArgumentException{
    public EventNotFoundException() {
        super("Not Fount Event");
    }
}
