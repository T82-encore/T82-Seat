package com.T82.ticket.service;

import com.T82.ticket.dto.request.EventInitRequestDto;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;

import java.util.List;

public interface InitService {
    void initEventPlace(EventInitRequestDto req);
}
