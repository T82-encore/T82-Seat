package com.T82.ticket.controller;

import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class SectionController {
    private final SectionService sectionService;

    @GetMapping("/events/{eventId}/restseats")
    @ResponseStatus(HttpStatus.OK)
    public List<RestSeatResponseDto> getAvailableSeatCountPerSection(@PathVariable(name = "eventId") Long eventId){
        return sectionService.getAvailableSeatCountPerSection(eventId);
    };
}
