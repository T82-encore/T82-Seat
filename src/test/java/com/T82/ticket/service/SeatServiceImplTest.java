package com.T82.ticket.service;

import com.T82.ticket.dto.response.AvailableSeatsResponseDto;
import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
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
    private Section section;
    private Long evnetId;
    @BeforeEach
    void setUp() {
        Place place = new Place(1L, "장소1", "주소1", new ArrayList<>());
        placeRepository.saveAndFlush(place);
        section = new Section(null, "구역이름1", 25L, 21L, 10000L, place, new ArrayList<>());
        sectionRepository.saveAndFlush(section);

        evnetId = section.getPlace().getEventId();

        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= 5; j++) {
                Seat seat = new Seat(null, (long) i, (long) j,false,false,section);
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
}