package com.T82.ticket.service;

import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.Seat;

import java.util.List;

public interface SeatService {
    List<AvailableSeatsResponseDto> getAvailableSeats(Long sectionId);
}
