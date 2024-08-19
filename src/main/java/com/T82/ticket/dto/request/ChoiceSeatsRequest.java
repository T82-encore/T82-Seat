package com.T82.ticket.dto.request;


import com.T82.ticket.global.domain.entity.SeatReservation;

import java.util.UUID;

public record ChoiceSeatsRequest(Long seatId , Long eventId , int price) {

    public SeatReservation toEntity(String userId){
        return SeatReservation.builder()
                .eventId(eventId)
                .seatId(seatId)
                .userId(UUID.fromString(userId))
                .price(price)
                .build();
    }
//    public ChoiceSeat toEntity(String userId){
//        return ChoiceSeat.builder()
//                .eventId(eventId)
//                .seatId(seatId)
//                .userId(UUID.fromString(userId))
//                .price(price)
//                .build();
//    }
}