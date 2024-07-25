package com.T82.ticket.service.impl;

import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.dto.request.EventInitRequestDto;
import com.T82.ticket.global.domain.entity.Place;
import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.repository.PlaceRepository;
import com.T82.ticket.global.domain.repository.SeatRepository;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.InitService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InitServiceImpl implements InitService {
    private final PlaceRepository placeRepository;
    private final SectionRepository sectionRepository;
    private final SeatRepository seatRepository;
    @KafkaListener(topics = "eventTopic")
    @Override
    @Transactional
    public void initEventPlace(EventInitRequestDto req) {

        if(req.seatAvailable()) {

            Place savedPlace = placeRepository.save(Place.toEntity(req));

            req.sectionInitRequest().forEach(sectionInitRequestDto -> {
                Section savedSection = sectionRepository.save(Section.toEntity(sectionInitRequestDto, savedPlace));
                int startRow = sectionInitRequestDto.startRow();
                int startCol = sectionInitRequestDto.startCol();
                int rowNum = sectionInitRequestDto.rowNum();
                int colNum = sectionInitRequestDto.colNum();

                for (int row = startRow; row < startRow + rowNum; row++) {
                    for (int col = startCol; col < startCol + colNum; col++) {
                        seatRepository.save(Seat.toEntity(row, col, savedSection));
                    }
                }
            });
        }

    }
}
