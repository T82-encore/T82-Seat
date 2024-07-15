package com.T82.ticket.dto.request;

public record SectionInitRequestDto(String sectionName, Integer startRow, Integer startCol, Integer price,Integer sectionTotalSeat,Integer rowNum,Integer colNum) {
}