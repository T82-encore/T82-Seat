package com.T82.ticket.service;

import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import com.T82.ticket.global.domain.repository.SeatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService{
    private final SeatRepository seatRepository;
    @Override
    public List<AvailableSeatsResponseDto> getAvailableSeats(Long sectionId) {
        List<Seat> allBySectionId = seatRepository.findAllBySectionId(sectionId);
        if(allBySectionId.isEmpty()) throw new SectionNotFoundException();
        return allBySectionId.stream().map(AvailableSeatsResponseDto::from).toList();
    }
}
