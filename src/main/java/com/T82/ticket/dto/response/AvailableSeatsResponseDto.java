package com.T82.ticket.dto.response;

import com.T82.ticket.global.domain.entity.Seat;

public record AvailableSeatsResponseDto(Long seatId, Long rowNum, Long columnNum) {
    public static AvailableSeatsResponseDto from(Seat seat){
        return new AvailableSeatsResponseDto(
                seat.getSeatId(),
                seat.getRowNum(),
                seat.getColumnNum()
        );
    }
}
