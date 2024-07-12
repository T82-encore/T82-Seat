package com.T82.ticket.controller;

import com.T82.ticket.global.domain.exception.EventNotFoundException;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(EventNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String eventNotFoundExceptionHandler(EventNotFoundException e){
        return e.getMessage();
    }

    @ExceptionHandler(SectionNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String sectionNotFoundExceptionHandler(SectionNotFoundException e){
        return e.getMessage();
    }

}
