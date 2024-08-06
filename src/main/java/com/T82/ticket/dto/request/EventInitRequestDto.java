package com.T82.ticket.dto.request;

import java.util.List;

public record EventInitRequestDto (Long eventId,
                                   String placeName,
                                   String address,
                                   boolean seatAvailable,
                                   Integer totalSeat,
                                   Integer totalRow,
                                   Integer totalCol,
                                   List<SectionInitRequestDto> sectionInitRequest
){

}
