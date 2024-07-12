package com.T82.ticket.global.domain.exception;

public class SectionNotFoundException extends IllegalArgumentException{
    public SectionNotFoundException() {
        super("Not Fount Section");
    }
}
