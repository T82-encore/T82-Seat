package com.T82.ticket.service.impl;

import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.dto.response.SeatDetailResponse;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.EventNotFoundException;
import com.T82.ticket.global.domain.exception.SeatAlreadyChosenException;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.exception.SectionNotFoundException;
import com.T82.ticket.global.domain.repository.ChoiceSeatRepository;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.SeatService;
import com.T82.ticket.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService , SectionService {

    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    private final ChoiceSeatRepository choiceSeatRepository;
    private final RedissonClient redissonClient;

    @Override
    public List<AvailableSeatsResponseDto> getAvailableSeats(Long eventId) {
        List<Seat> seats = sectionRepository.findAllSeatsByEventId(eventId);
        if(seats.isEmpty()) throw new SectionNotFoundException();
        return seats.stream().map(AvailableSeatsResponseDto::from).toList();
    }

    @Override
    @Transactional
    public void choiceSeats(List<ChoiceSeatsRequest> req, String userId) {
        boolean InvalidSeats =req.stream()
                        .map(dto -> seatRepository.findById(dto.seatId())
                                        .orElseThrow(SeatNotFoundException::new))
                                .anyMatch(seat -> seat.getIsChoicing() || seat.getIsBooked());
        if (InvalidSeats) throw new SeatAlreadyChosenException();

        req.forEach(dto -> lockAndProcessSeat(dto, userId));
    }

    public void lockAndProcessSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
        String lockKey = "lock:seat:" + choiceSeatsRequest.seatId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(5, 360, TimeUnit.SECONDS);
            if (isLocked) {
                processSeat(choiceSeatsRequest, userId);
            } else {
                throw new RuntimeException("락 획득 실패 " + lockKey);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("락 획득 중 인터럽트 발생: " + lockKey, e);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
    @Transactional
    public void processSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
        Seat seat = seatRepository.findById(choiceSeatsRequest.seatId())
                .orElseThrow(SeatNotFoundException::new);

        if (seat.getIsChoicing() || seat.getIsBooked()) {
            throw new SeatAlreadyChosenException();
        }

        seat.setIsChoicing(true);
        seatRepository.save(seat); // 선택중으로 변경된 상태 저장
        choiceSeatRepository.save(choiceSeatsRequest.toEntity(userId));
    }

    @Override
    public List<SeatDetailResponse> seatDetailResponses(List<Long> seatIds){
       return seatIds.stream()
                .map(seatId ->{
                   Seat seat = seatRepository.findById(seatId)
                           .orElseThrow(SeatNotFoundException::new);
                    Section section = sectionRepository.findById(seat.getSection().getSectionId())
                            .orElseThrow(SectionNotFoundException :: new);
                    return SeatDetailResponse.from(seat,section);
                }).toList();
    }

    @Override
    public List<RestSeatResponseDto> getAvailableSeatCountPerSection(Long eventId) {
        List<Section> allByEventId = sectionRepository.findAllByEventId(eventId);
        if(allByEventId.isEmpty()) throw new EventNotFoundException();
        return allByEventId.stream().map(RestSeatResponseDto::from).toList();
    }
}
