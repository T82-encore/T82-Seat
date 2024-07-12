package com.T82.ticket.global.domain.exception;

public class NotFoundEventIdException extends IllegalArgumentException{
    public NotFoundEventIdException() {
        super("Not Fount Event");
    }
}
