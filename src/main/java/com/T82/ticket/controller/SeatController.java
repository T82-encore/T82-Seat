package com.T82.ticket.controller;

import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/events/{eventId}")
    public List<AvailableSeatsResponseDto> getAvailableSeats(@PathVariable(name = "eventId") Long eventId){
        return seatService.getAvailableSeats(eventId);
    }
}
