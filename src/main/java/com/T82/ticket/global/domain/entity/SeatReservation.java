package com.T82.ticket.global.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash(value = "Seats" ,timeToLive = 360)
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SeatReservation implements Serializable {

    @Id
    private Long seatId;

    private UUID userId;

    private Long eventId;

    private int price;

}
