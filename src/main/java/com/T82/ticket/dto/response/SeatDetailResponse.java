package com.T82.ticket.dto.response;

import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import lombok.Builder;

@Builder
public record SeatDetailResponse(Long seatId,String seatSection,int seatRowNumber ,int seatColumNumber) {


    public static SeatDetailResponse from(Seat seat , Section section){
        return SeatDetailResponse.builder()
                .seatId(seat.getSeatId())
                .seatSection(section.getName())
                .seatRowNumber(seat.getRowNum())
                .seatColumNumber(seat.getColNum())
                .build();

    }
}
