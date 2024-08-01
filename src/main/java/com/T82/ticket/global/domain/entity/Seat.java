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
    private Integer rowNum;
    @Column(name = "COLUMN_NUM")
    private Integer colNum;
    @Column(name = "IS_BOOKED") @Setter
    private Boolean isBooked;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SECTION_ID")
    private Section section;

    public static Seat toEntity(int row, int col, Section section){
        return Seat.builder()
                .seatId(null)
                .rowNum(row)
                .colNum(col)
                .isBooked(false)
                .section(section)
                .build();
    }

    public static void SeatBook(Seat seat){
        seat.setIsBooked(true);
    }

    public static void SeatRefund(Seat seat){
        seat.setIsBooked(false);
    }
}
