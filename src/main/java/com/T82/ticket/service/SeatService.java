package com.T82.ticket.service;

import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.dto.response.SeatDetailResponse;

import java.util.List;

public interface SeatService {
    List<AvailableSeatsResponseDto> getAvailableSeats(Long sectionId);

    void choiceSeats(List<ChoiceSeatsRequest> req, String id);

    List<SeatDetailResponse> seatDetailResponses(List<Long> seatIds);
}
