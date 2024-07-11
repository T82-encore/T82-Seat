package com.T82.ticket.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SEATS")
public class Seat {
    @Id
    @Column(name = "SEAT_ID")
    private Long seatId;
    @Column(name = "ROW_NUM")
    private Long rowNum;
    @Column(name = "COLUMN_NUM")
    private Long columnNum;
    @Column(name = "IS_CHOICING")
    private Boolean isChoicing;
    @Column(name = "IS_BOOKED")
    private Boolean isBooked;
    @ManyToOne
    @JoinColumn(name = "SECTION_ID")
    private Section section;
}
