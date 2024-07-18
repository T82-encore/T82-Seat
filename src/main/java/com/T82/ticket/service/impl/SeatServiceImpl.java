package com.T82.ticket.service.impl;

import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.exception.SeatAlreadyChosenException;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import com.T82.ticket.global.domain.repository.ChoiceSeatRepository;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.SeatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    private final ChoiceSeatRepository choiceSeatRepository;
    @Override
    public List<AvailableSeatsResponseDto> getAvailableSeats(Long eventId) {
        List<Seat> seats = sectionRepository.findAllSeatsByEventId(eventId);
        if(seats.isEmpty()) throw new SectionNotFoundException();
        return seats.stream().map(AvailableSeatsResponseDto::from).toList();
    }

    @Override
    @Transactional
    public void choiceSeats(List<ChoiceSeatsRequest> req, String id) {

        req.forEach(dto->{
            //좌석을 찾고
            Seat seat = seatRepository.findById(dto.seatId()).orElseThrow(SeatNotFoundException::new);
            // 좌석이 이미 선택 됐을 경우
            if(seat.getIsChoicing()) throw new SeatAlreadyChosenException();
            //선택중으로 바꾸고
            seat.setIsChoicing(true);
            //pending 테이블에 정보를 저장
            choiceSeatRepository.save(dto.toEntity(id));
        });
    }

}
