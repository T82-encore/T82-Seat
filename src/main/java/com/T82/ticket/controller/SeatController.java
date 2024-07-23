package com.T82.ticket.controller;


import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.dto.response.SeatDetailResponse;
import com.T82.ticket.global.domain.exception.MaxSeatsException;
import com.T82.ticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class SeatController {
    private final SeatService seatService;

    @GetMapping("/events/{eventId}/seats")
    public List<AvailableSeatsResponseDto> getAvailableSeats(@PathVariable(name = "eventId") Long eventId){
        return seatService.getAvailableSeats(eventId);
    }

    @PutMapping("/seats/choice")
    public void choiceSeats(@AuthenticationPrincipal TokenInfo tokenInfo,
            @RequestBody List<ChoiceSeatsRequest> req){
        if(req.size() > 5) throw new MaxSeatsException("예매 가능한 좌석의 수는 최대 5개입니다.");
        seatService.choiceSeats(req ,tokenInfo.id());
    }
    @PostMapping("/seats/detail")
    public List<SeatDetailResponse> getDetails(List<Long> seatIds){
        return seatService.seatDetailResponses(seatIds);
    }

}


