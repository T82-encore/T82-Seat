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

    @Transactional
    public void lockAndProcessSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
        String lockKey = "lock:seat:" + choiceSeatsRequest.seatId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(5, 300, TimeUnit.SECONDS);
            if (isLocked) {
                Seat seat = seatRepository.findById(choiceSeatsRequest.seatId())
                        .orElseThrow(SeatNotFoundException::new);

                synchronized (seat) {
                    if (seat.getIsChoicing()) {
                        throw new SeatAlreadyChosenException();
                    }
                    seat.setIsChoicing(true);
                    seatRepository.save(seat); // 선택중으로 변경된 상태 저장
                    choiceSeatRepository.save(choiceSeatsRequest.toEntity(userId));
                }
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
}
