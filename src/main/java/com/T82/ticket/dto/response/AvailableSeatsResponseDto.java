package com.T82.ticket.dto.response;

import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;

public record AvailableSeatsResponseDto(Long seatId, Long rowNum, Long columnNum, String name, Long price) {
    public static AvailableSeatsResponseDto from(Seat seat){
        Section section = seat.getSection();
        return new AvailableSeatsResponseDto(
                seat.getSeatId(),
                seat.getRowNum(),
                seat.getColumnNum(),
                section.getName(),
                section.getPrice()
        );
    }
}
