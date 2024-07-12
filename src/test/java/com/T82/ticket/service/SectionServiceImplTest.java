package com.T82.ticket.service;

import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.repository.PlaceRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
class SectionServiceImplTest {

    @Autowired
    SectionRepository sectionRepository;
    @Autowired
    PlaceRepository placeRepository;

    @Autowired
    SectionServiceImpl sectionService;

    private Place place;

    @BeforeEach
    void setUp() {
        place = new Place(1L,"장소1","주소1",new ArrayList<>());
        placeRepository.saveAndFlush(place);

        Section section1 = new Section(null, "이벤트이름1", 100L, 80L, 1000L, place, new ArrayList<>());
        Section section2 = new Section(null, "이벤트이름2", 200L, 150L, 1500L, place, new ArrayList<>());
        Section section3 = new Section(null, "이벤트이름3", 150L, 50L, 1200L, place, new ArrayList<>());

        sectionRepository.saveAndFlush(section1);
        sectionRepository.saveAndFlush(section2);
        sectionRepository.saveAndFlush(section3);
    }

    @Test
    @DisplayName("이벤트ID로 이벤트의 모든 구역들의 남은 좌석 가져오기")
    @Transactional
    void getAvailableSeatCountPerSectionTest() {
//    when
        List<RestSeatResponseDto> availableSeatCountPerSection = sectionService.getAvailableSeatCountPerSection(1L);
//    then
        assertNotNull(availableSeatCountPerSection);
        assertEquals(3, availableSeatCountPerSection.size());

        assertTrue(availableSeatCountPerSection.stream()
                .anyMatch(dto -> dto.name().equals("이벤트이름1") && dto.restSeat().equals(80L)));
        assertTrue(availableSeatCountPerSection.stream()
                .anyMatch(dto -> dto.name().equals("이벤트이름2") && dto.restSeat().equals(150L)));
        assertTrue(availableSeatCountPerSection.stream()
                .anyMatch(dto -> dto.name().equals("이벤트이름3") && dto.restSeat().equals(50L)));
    }
}