package com.T82.ticket.dto.response;

import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;

public record AvailableSeatsResponseDto(Long seatId, Integer rowNum, Integer colNum, String name, Integer price) {
    public static AvailableSeatsResponseDto from(Seat seat){
        Section section = seat.getSection();
        return new AvailableSeatsResponseDto(
                seat.getSeatId(),
                seat.getRowNum(),
                seat.getColNum(),
                section.getName(),
                section.getPrice()
        );
    }
}
