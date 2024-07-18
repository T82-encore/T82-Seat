package com.T82.ticket.global.domain.exception;

public class SeatAlreadyChosenException  extends IllegalArgumentException{
    public SeatAlreadyChosenException () {
        super("이미 예약된 좌석입니다");
    }
}
