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
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService {

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
    public void choiceSeats(List<ChoiceSeatsRequest> req, String userId) {
        req.forEach(dto -> lockAndProcessSeat(dto, userId));
    }

    public void lockAndProcessSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
        String lockKey = "lock:seat:" + choiceSeatsRequest.seatId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            // 락 획득 시도
            isLocked = lock.tryLock( 5, 300, TimeUnit.SECONDS);
            if (isLocked) {
                // 좌석을 찾고
                Seat seat = seatRepository.findById(choiceSeatsRequest.seatId()).orElseThrow(SeatNotFoundException::new);
                // 좌석이 이미 선택됐을 경우
                if (seat.getIsChoicing()) throw new SeatAlreadyChosenException();
                // 선택중으로 바꾸고
                seat.setIsChoicing(true);
                // pending 테이블에 정보를 저장
                choiceSeatRepository.save(choiceSeatsRequest.toEntity(userId));
            } else {
                throw new RuntimeException("Could not acquire lock for key: " + lockKey);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to acquire lock for key: " + lockKey, e);
        } finally {
            if (isLocked) {
                lock.unlock();
            }
        }
    }
}
