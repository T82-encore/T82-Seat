package com.T82.ticket.dto.response;

import com.T82.ticket.global.domain.entity.Section;

public record RestSeatResponseDto (Long sectionId, String name,Integer restSeat){
    public static RestSeatResponseDto from(Section section){
        return new RestSeatResponseDto(
                section.getSectionId(),
                section.getName(),
                section.getRestSeat()
        );
    }
}

