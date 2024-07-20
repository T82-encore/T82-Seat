package com.T82.ticket.service.impl;

import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.ChoiceSeat;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.SeatAlreadyChosenException;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.repository.ChoiceSeatRepository;
import com.T82.ticket.global.domain.repository.PlaceRepository;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class SeatServiceImplTest {

    @Autowired
    SeatRepository seatRepository;
    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    PlaceRepository placeRepository;
    @Autowired
    SeatServiceImpl seatService;
    @Autowired
    ChoiceSeatRepository choiceSeatRepository;

    @PersistenceContext
    private EntityManager entityManager;
    private TokenInfo tokenInfo;
    private Section section;

    private Seat seat;
    private Long evnetId;
    private List<Seat> seats;

    @BeforeEach
    void setUp() {
        seatRepository.deleteAll();
        choiceSeatRepository.deleteAll();

        Place place = new Place(1L, "장소1", "주소1", 50, 50, new ArrayList<>());
        placeRepository.saveAndFlush(place);
        section = new Section(null, "구역이름1", 21, 10000, 0, 0, 1, 1, place, new ArrayList<>());
        sectionRepository.saveAndFlush(section);

        seats = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                Seat seat = new Seat(null, i, j, false, false, section);
                seatRepository.saveAndFlush(seat);
                seats.add(seat);
            }
        }

        // 로그 추가: 초기화된 좌석 정보 출력
        System.out.println("Initialized seats:");
        seats.forEach(seat -> System.out.println("Seat ID: " + seat.getSeatId() + ", Row: " + seat.getRowNum() + ", Column: " + seat.getColNum()));
    }

    @Nested
    @Transactional
    class 구역ID로_해당구역의_예약가능한_모든_좌석_반환 {
        @Test
        void 예약된_좌석_제외하고_반환() {
//             두 개의 좌석의 isBooked를 true로 설정
            List<Seat> seats = seatRepository.findAll();
            seats.get(0).setIsBooked(true);
            seats.get(1).setIsBooked(true);
            seatRepository.saveAllAndFlush(seats);
//    when
            List<AvailableSeatsResponseDto> availableSeats = seatService.getAvailableSeats(evnetId);
//    then
            assertEquals(23, availableSeats.size());
        }

        @Test
        void 선택된_좌석_제외하고_반환() {
//             두 개의 좌석의 isChoicing을 true로 설정
            List<Seat> seats = seatRepository.findAll();
            seats.get(0).setIsChoicing(true);
            seats.get(1).setIsChoicing(true);
            seats.get(2).setIsChoicing(true);
            seatRepository.saveAllAndFlush(seats);
//    when
            List<AvailableSeatsResponseDto> availableSeats = seatService.getAvailableSeats(evnetId);
//    then
            assertEquals(22, availableSeats.size());
        }
    }

    @Nested
    class 좌석을_선택하면_해당_좌석을_선택하지_못하도록하는_기능 {
        @Test
        @Transactional
        void seatId가_존재하지않는_좌석일때_예외테스트() {
//    given
            ChoiceSeatsRequest test1 = new ChoiceSeatsRequest(100000L, 1L, 150000);
            List<ChoiceSeatsRequest> req = new ArrayList<>();
            req.add(test1);
            UUID userId = UUID.randomUUID(); // UUID 생성
//    when
            SeatNotFoundException seatNotFoundException = assertThrows(SeatNotFoundException.class, () -> seatService.choiceSeats(req, userId.toString()));
//    then
            assertEquals("Not Fount Seat", seatNotFoundException.getMessage());
        }

        @Test
        @Transactional
        void 이미_선택된_좌석일때_예외테스트() {
            // given
            List<Seat> seats = seatRepository.findAll();
            seats.get(0).setIsChoicing(true);
            seatRepository.saveAllAndFlush(seats);

            UUID userId = UUID.randomUUID(); // UUID 생성

            ChoiceSeatsRequest test1 = new ChoiceSeatsRequest(seats.get(0).getSeatId(), 1L, 150000);
            List<ChoiceSeatsRequest> req = new ArrayList<>();
            req.add(test1);

            // when
            SeatAlreadyChosenException seatAlreadyChosenException = assertThrows(SeatAlreadyChosenException.class, () -> seatService.choiceSeats(req, userId.toString()));

            // then
            assertEquals("이미 예약된 좌석입니다", seatAlreadyChosenException.getMessage());
        }

        @Test
        @Transactional
        void 정상적으로_좌석을_선택() {
            // given
            List<Seat> seats = seatRepository.findAll();

            // 모든 좌석의 선택 상태를 초기화
            seats.forEach(seat -> seat.setIsChoicing(false));
            seatRepository.saveAllAndFlush(seats);

            UUID userId = UUID.randomUUID(); // UUID 생성

            ChoiceSeatsRequest test1 = new ChoiceSeatsRequest(seats.get(0).getSeatId(), 1L, 150000);
            ChoiceSeatsRequest test2 = new ChoiceSeatsRequest(seats.get(1).getSeatId(), 2L, 150000);
            List<ChoiceSeatsRequest> req = new ArrayList<>();
            req.add(test1);
            req.add(test2);

            // when
            seatService.choiceSeats(req, userId.toString());

            // then
            assertEquals(true, seatRepository.findById(seats.get(0).getSeatId()).get().getIsChoicing());
            assertEquals(true, seatRepository.findById(seats.get(1).getSeatId()).get().getIsChoicing());

            List<ChoiceSeat> choiceSeats = choiceSeatRepository.findAll();
            assertEquals(2, choiceSeats.size());
            assertEquals(test1.seatId(), choiceSeats.get(0).getSeatId());
            assertEquals(test2.seatId(), choiceSeats.get(1).getSeatId());
        }

        @Test
        @DisplayName("동시성 테스트: choiceSeats 메서드")
        void choiceSeats_동시성_테스트() throws InterruptedException {
            // given
            seats.forEach(seat -> seat.setIsChoicing(false));
            seatRepository.saveAllAndFlush(seats);

            int n = 100;
            List<ChoiceSeatsRequest> requests = new ArrayList<>();
            ChoiceSeatsRequest choiceSeatsRequest = new ChoiceSeatsRequest(seats.get(0).getSeatId(), 1L, 150000);
            requests.add(choiceSeatsRequest);

            ExecutorService executorService = Executors.newFixedThreadPool(n);
            Runnable[] tasks = new Runnable[n];

            for (int i = 0; i < n; i++) {
                UUID userId = UUID.randomUUID();

                Runnable task = () -> {
                    try {
                        seatService.choiceSeats(requests, userId.toString());
                    } catch (Exception e) {
                        System.out.println("Exception in thread: " + e.getMessage());
                    }
                };
                tasks[i] = task;
            }

            for (int i = 0; i < n; i++) {
                executorService.submit(tasks[i]);
            }

            Thread.sleep(10000);
            executorService.shutdown();
            executorService.awaitTermination(10, TimeUnit.SECONDS);

            System.out.println("테스트 완료");

            // then
            List<ChoiceSeat> choiceSeats = choiceSeatRepository.findAll();
            assertEquals(1, choiceSeats.size(), "Only one seat choice should be recorded");
        }
    }
}