package com.T82.ticket.service.impl;

import com.T82.ticket.dto.response.RestSeatResponseDto;
import com.T82.ticket.global.domain.entity.Section;
import com.T82.ticket.global.domain.exception.EventNotFoundException;
import com.T82.ticket.global.domain.repository.SectionRepository;
import com.T82.ticket.service.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class SectionServiceImpl implements SectionService {
    private final SectionRepository sectionRepository;

    @Override
    public List<RestSeatResponseDto> getAvailableSeatCountPerSection(Long eventId) {
        List<Section> allByEventId = sectionRepository.findAllByEventId(eventId);
        if(allByEventId.isEmpty()) throw new EventNotFoundException();
        return allByEventId.stream().map(RestSeatResponseDto::from).toList();
    }
}
