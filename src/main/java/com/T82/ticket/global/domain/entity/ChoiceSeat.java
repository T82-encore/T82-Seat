package com.T82.ticket.global.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "CHOICE_SEATS")
public class ChoiceSeat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long choiceSeatId;
    @Column(name = "EVENT_ID")
    Long eventId;
    @Column(name = "SEAT_ID")
    Long seatId;
    @Column(name = "USER_ID")
    UUID userId;
    @Column(name = "PRICE")
    Integer price;

}
