package com.T82.ticket.dto.request;


import com.T82.ticket.global.domain.entity.ChoiceSeat;

import java.util.UUID;

public record ChoiceSeatsRequest(Long seatId , Long eventId , Integer price) {

    public ChoiceSeat toEntity(String userId){
        return ChoiceSeat.builder()
                .eventId(eventId)
                .seatId(seatId)
                .userId(UUID.fromString(userId))
                .price(price)
                .build();
    }


}