package com.T82.ticket.controller;

import com.T82.ticket.dto.request.EventInitRequestDto;
import com.T82.ticket.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class PlaceController {
    private final InitService initService;

    @PostMapping("/event/{eventId}/init")
    @ResponseStatus(HttpStatus.OK)
    public void initPlace(@RequestBody EventInitRequestDto req){
        initService.initEventPlace(req);
    };
}
