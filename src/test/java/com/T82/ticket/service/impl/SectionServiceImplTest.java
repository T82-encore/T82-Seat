package com.T82.ticket.service.impl;

import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.EventNotFoundException;
import com.T82.ticket.global.domain.repository.PlaceRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.impl.SectionServiceImpl;
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
        place = new Place(1L, "장소1", "주소1",50 ,50,new ArrayList<>());
        placeRepository.saveAndFlush(place);

        Section section1 = new Section(null, "구역이름1", 20, 10000,0, 0 ,1,1,place, new ArrayList<>());
        Section section2 = new Section(null, "구역이름2", 10, 110000,5, 5 ,1,1,place, new ArrayList<>());
        Section section3 = new Section(null, "구역이름3", 5, 150000,10, 10 ,1,1,place, new ArrayList<>());

        sectionRepository.saveAndFlush(section1);
        sectionRepository.saveAndFlush(section2);
        sectionRepository.saveAndFlush(section3);
    }

    @Nested
    @Transactional
    class 이벤트하나의_모든_구역의_남은_좌석수_가져오기{
        @Test
        void 이벤트ID로_이벤트의_모든_구역들의_남은_좌석_가져오기() {
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

        @Test
        void 존재하지_않는_이벤트ID일때() {
            //    when
            EventNotFoundException notFoundEventException = assertThrows(EventNotFoundException.class,()-> sectionService.getAvailableSeatCountPerSection(100000L));
            // then
            assertEquals("Not Fount Event",notFoundEventException.getMessage());
        }
    }

}