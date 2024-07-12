package com.T82.ticket.controller;

import com.T82.ticket.global.domain.exception.NotFoundEventIdException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {
    @ExceptionHandler(NotFoundEventIdException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String accountNotFoundExceptionHandler(NotFoundEventIdException e){
        return e.getMessage();
    }


}
