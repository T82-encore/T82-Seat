package com.T82.ticket.global.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "SECTIONS")
public class Section {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SECTION_ID")
    private Long sectionId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "TOTAL_SEAT")
    private Long totalSeat;
    @Column(name = "REST_SEAT")
    private Long restSeat;
    @Column(name = "PRICE")
    private Long price;

    @ManyToOne
    @JoinColumn(name = "PLACE_ID")
    private Place place;

    @OneToMany(mappedBy = "section")
    private List<Seat> seats = new ArrayList<>();
}
