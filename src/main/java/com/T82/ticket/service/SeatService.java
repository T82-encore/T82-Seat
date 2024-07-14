package com.T82.ticket.service;

import com.T82.ticket.dto.request.ChoiceSeatsRequestDto;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;

import java.util.List;

public interface SeatService {
    List<AvailableSeatsResponseDto> getAvailableSeats(Long sectionId);

    void choiceSeats(List<ChoiceSeatsRequestDto> req);
}
