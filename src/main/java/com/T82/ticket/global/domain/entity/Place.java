package com.T82.ticket.global.domain.entity;

import com.T82.ticket.dto.request.EventInitRequestDto;
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
@Table(name = "PLACES")
public class Place {
    @Id
    @Column(name = "EVENT_ID")
    private Long eventId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADDRESS")
    private String address;
    @Column(name = "TOTAL_ROW")
    private Integer totalRow;
    @Column(name = "TOTAL_COL")
    private Integer totalCol;
    @OneToMany(mappedBy = "place")
    private List<Section> sections = new ArrayList<>();

    public static Place toEntity(EventInitRequestDto req){
        return Place.builder()
                .eventId(req.eventId())
                .name(req.placeName())
                .address(req.address())
                .totalRow(req.totalRow())
                .totalCol(req.totalCol())
                .build();
    }

}