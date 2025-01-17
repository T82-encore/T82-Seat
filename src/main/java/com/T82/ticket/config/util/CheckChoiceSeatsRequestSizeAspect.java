package com.T82.ticket.config.util;

import com.T82.common_exception.exception.seat.MaxAvailableSeatsExceededException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.List;

@Aspect
@Component
public class CheckChoiceSeatsRequestSizeAspect {
    @Before("@annotation(CheckChoiceSeatsRequestSize)")
    public void checkRequestSize(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();

        for (Object arg : args){
            if(arg instanceof List<?> list){
                if(list.size() > 5) throw new MaxAvailableSeatsExceededException();
            }
        }
    }
}
