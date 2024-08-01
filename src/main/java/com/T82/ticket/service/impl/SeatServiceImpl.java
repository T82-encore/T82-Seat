package com.T82.ticket.service.impl;

import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.request.RefundSeatRequest;
import com.T82.ticket.dto.request.SeatDetailRequest;
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
import org.springframework.kafka.annotation.KafkaListener;
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
    public void choiceSeats(List<ChoiceSeatsRequest> req, String userId) {
        req.forEach(dto -> lockAndProcessSeat(dto, userId));
    }

    @Transactional
    public void lockAndProcessSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
        String lockKey = "lock:seat:" + choiceSeatsRequest.seatId();
        RLock lock = redissonClient.getLock(lockKey);

        boolean isLocked = false;
        try {
            isLocked = lock.tryLock(5, 360, TimeUnit.SECONDS);
            if (isLocked) {
                Seat seat = seatRepository.findById(choiceSeatsRequest.seatId())
                        .orElseThrow(SeatNotFoundException::new);

                if(choiceSeatRepository.findBySeatId(choiceSeatsRequest.seatId()) != null)
                    throw new SeatAlreadyChosenException();

                synchronized (seat) {
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
    @Override
    @Transactional
    public List<SeatDetailResponse> seatDetailResponses(SeatDetailRequest seatIds){
        return seatIds.getSeatIds().stream()
                .map(seatId ->{
                    Seat seat = seatRepository.findById(seatId)
                            .orElseThrow(SeatNotFoundException::new);
                    log.info("seatId" + seat.getSeatId());

                    Seat.SeatBook(seat);

                    Section section = sectionRepository.findById(seat.getSection().getSectionId())
                            .orElseThrow(SectionNotFoundException :: new);

                    Section.DecreaseInSectionSeats(section);

                    return SeatDetailResponse.from(seat,section);
                }).toList();
    }


    @Override
    public List<RestSeatResponseDto> getAvailableSeatCountPerSection(Long eventId) {
        List<Section> allByEventId = sectionRepository.findAllByEventId(eventId);
        if(allByEventId.isEmpty()) throw new EventNotFoundException();
        return allByEventId.stream().map(RestSeatResponseDto::from).toList();
    }

    @KafkaListener(topics = "refundSeat", groupId = "refundSeat-group")
    @Transactional
    public void seatRefund (RefundSeatRequest refundSeatRequest){
        Seat seat = seatRepository.findById(refundSeatRequest.getSeatId())
                .orElseThrow(SeatNotFoundException :: new);

        Seat.SeatRefund(seat);
        log.info("Seat refund processed for seatId: {}", refundSeatRequest.getSeatId());
        Section section =sectionRepository.findById(seat.getSection().getSectionId())
                .orElseThrow(SectionNotFoundException :: new);

        Section.IncreaseInSectionSeats(section);
        log.info("Increased section seats for sectionId: {}", seat.getSection().getRestSeat());
    }

}
