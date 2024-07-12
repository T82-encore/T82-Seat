package com.T82.ticket.service;

import com.T82.ticket.dto.response.RestSeatResponseDto;

import java.util.List;

public interface SectionService {
    List<RestSeatResponseDto> getAvailableSeatCountPerSection(Long eventId);
}
