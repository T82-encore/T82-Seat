package com.T82.ticket.service.impl;

import com.T82.common_exception.annotation.CustomException;
import com.T82.common_exception.annotation.ExecutionTimeLog;
import com.T82.common_exception.exception.ErrorCode;
import com.T82.common_exception.exception.seat.EventNotFoundException;
import com.T82.common_exception.exception.seat.SeatAlreadyChosenException;
import com.T82.common_exception.exception.seat.SeatNotFoundException;
import com.T82.common_exception.exception.seat.SectionNotFoundException;
import com.T82.ticket.config.util.LockSeat;
import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.request.RefundSeatRequest;
import com.T82.ticket.dto.request.SeatDetailRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.dto.response.SeatDetailResponse;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SeatReservationRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.SeatService;
import com.T82.ticket.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SeatServiceImpl implements SeatService , SectionService {

    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    private final SeatReservationRepository seatReservationRepository;

    @Override
    @CustomException(ErrorCode.SECTION_NOT_FOUND)
    public List<AvailableSeatsResponseDto> getAvailableSeats(Long eventId) {

        long start1 = System.currentTimeMillis();
        List<Seat> seats = sectionRepository.findAllSeatsByEventId(eventId);

        if(seats.isEmpty()) throw new SectionNotFoundException();
        log.info("시간 : {}", System.currentTimeMillis() - start1);

        // 조회할 seatId 목록을 추출
        List<Long> seatIds = seats.stream()
                .map(Seat::getSeatId)
                .toList();

        long start2 = System.currentTimeMillis();
        // 해당 좌석 ID 중 이미 예약된 것들을 한 번에 조회
        List<Long> pending = new ArrayList<>();
        seatReservationRepository.findAllById(seatIds).forEach(seatReservation -> pending.add(seatReservation.getSeatId()));
        log.info("시간 : {}", System.currentTimeMillis() - start2);

        // 예약된 좌석 ID를 추출
//        Set<Long> reservedSeatIds = reservedSeats.stream()
//                .map(SeatReservation::getSeatId)
//                .collect(Collectors.toSet());

        long start = System.currentTimeMillis();
        // 예약되지 않은 좌석만 필터링
        List<AvailableSeatsResponseDto> result = seats.stream()
                .filter(seat -> !pending.contains(seat.getSeatId()))
                .map(AvailableSeatsResponseDto::from)
                .toList();
        log.info("시간 : {}", System.currentTimeMillis() - start);
        return result;
    }



    @Transactional
    @CustomException(ErrorCode.SEAT_NOT_FOUND)
    @LockSeat
    public void choiceSeats(List<ChoiceSeatsRequest> req, String userId) {
        req.forEach(choiceSeatsRequest -> {
            Seat seat = seatRepository.findById(choiceSeatsRequest.seatId())
                    .orElseThrow(SeatNotFoundException::new);

            if (seatReservationRepository.findById(choiceSeatsRequest.seatId()).isPresent()) {
                throw new SeatAlreadyChosenException();
            }
                seatReservationRepository.save(choiceSeatsRequest.toEntity(userId));
        });
    }

//    @Transactional
//    @CustomException(ErrorCode.SEAT_NOT_FOUND)
//    public void lockAndProcessSeat(ChoiceSeatsRequest choiceSeatsRequest, String userId) {
//        String lockKey = "lock:seat:" + choiceSeatsRequest.seatId();
//        RLock lock = redissonClient.getLock(lockKey);
//
//        boolean isLocked = false;
//        try {
//            isLocked = lock.tryLock(5, 360, TimeUnit.SECONDS);
//            if (isLocked) {
//                Seat seat = seatRepository.findById(choiceSeatsRequest.seatId())
//                        .orElseThrow(SeatNotFoundException::new);
//
//                if(choiceSeatRepository.findBySeatId(choiceSeatsRequest.seatId()) != null)
//                    throw new SeatAlreadyChosenException();
//
//                synchronized (seat) {
//                    choiceSeatRepository.save(choiceSeatsRequest.toEntity(userId));
//                }
//            } else {
//                throw new RuntimeException("락 획득 실패 " + lockKey);
//            }
//        } catch (InterruptedException e) {
//            throw new RuntimeException("락 획득 중 인터럽트 발생: " + lockKey, e);
//        } finally {
//            if (isLocked) {
//                lock.unlock();
//            }
//        }
//    }

    @Override
    @Transactional
    @CustomException(ErrorCode.FAILED_GENERATE_TICKET)
    @ExecutionTimeLog
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
  
    @KafkaListener(topics = "refundSeat")
    @Transactional
    @CustomException(ErrorCode.FAILED_REFUND_TICKET)
    public void seatRefund (RefundSeatRequest refundSeatRequest){
        Seat seat = seatRepository.findById(refundSeatRequest.getSeatId())
                .orElseThrow(SeatNotFoundException :: new);

        Seat.SeatRefund(seat);
        log.info("Seat refund processed for seatId: {}", refundSeatRequest.getSeatId());
        Section section =sectionRepository.findById(seat.getSection().getSectionId())
                .orElseThrow(SectionNotFoundException:: new);

        Section.IncreaseInSectionSeats(section);
        log.info("Increased section seats for sectionId: {}", seat.getSection().getRestSeat());
    }
}
