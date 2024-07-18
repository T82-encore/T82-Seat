package com.T82.ticket.service.impl;

import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.SeatNotFoundException;
import com.T82.ticket.global.domain.repository.ChoiceSeatRepository;
import com.T82.ticket.global.domain.repository.PlaceRepository;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

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


    private TokenInfo tokenInfo;
    private Section section;

    private Seat seat;
    private Long evnetId;
    @BeforeEach
    void setUp() {
        Place place = new Place(1L, "장소1", "주소1",50 ,50,new ArrayList<>());
        placeRepository.saveAndFlush(place);
        section = new Section(null, "구역이름1", 21, 10000,0, 0 ,1,1,place, new ArrayList<>());
        sectionRepository.saveAndFlush(section);


        evnetId = section.getPlace().getEventId();

        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                Seat seat = new Seat(null,  i,  j,false,false,section);
                seatRepository.saveAndFlush(seat);
            }
        }
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
            assertEquals(23,availableSeats.size());
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
            assertEquals(22,availableSeats.size());
        }
    }

    @Nested
    @Transactional
    class 좌석을_선택하면_해당_좌석을_선택하지_못하도록하는_기능 {
        @Test
        @Transactional
        void seatId가_존재하지않는_좌석일때_예외테스트() {
//    given
            ChoiceSeatsRequest test1 = new ChoiceSeatsRequest(100000L, 1L,150000);
            List<ChoiceSeatsRequest> req = new ArrayList<>();
            req.add(test1);
//    when
            SeatNotFoundException seatNotFoundException = assertThrows(SeatNotFoundException.class,()-> seatService.choiceSeats(req, tokenInfo.id()));
//    then
            assertEquals("Not Fount Seat",seatNotFoundException.getMessage());
        }
    }
}