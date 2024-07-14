package com.T82.ticket.global.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SEATS")
public class Seat {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long seatId;
    @Column(name = "ROW_NUM")
    private Long rowNum;
    @Column(name = "COLUMN_NUM")
    private Long columnNum;
    @Column(name = "IS_CHOICING") @Setter
    private Boolean isChoicing;
    @Column(name = "IS_BOOKED") @Setter
    private Boolean isBooked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTION_ID")
    private Section section;

}
