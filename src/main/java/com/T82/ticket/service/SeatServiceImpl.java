package com.T82.ticket.service;

import com.T82.ticket.dto.request.ChoiceSeatsRequestDto;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService{
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    @Override
    public List<AvailableSeatsResponseDto> getAvailableSeats(Long eventId) {
        List<Seat> seats = sectionRepository.findAllSeatsByEventId(eventId);
        if(seats.isEmpty()) throw new SectionNotFoundException();
        return seats.stream().map(AvailableSeatsResponseDto::from).toList();
    }

    @Override
    @Transactional
    public void choiceSeats(List<ChoiceSeatsRequestDto> req) {
        req.forEach(dto->{
            Seat seat = seatRepository.findById(dto.seatId()).orElseThrow(SeatNotFoundException::new);
            seat.setIsChoicing(true);
        });
    }
}
