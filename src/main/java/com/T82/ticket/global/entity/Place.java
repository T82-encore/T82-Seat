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
@Table(name = "PLACES")
public class Place {
    @Id
    @Column(name = "EVENT_ID")
    private Long eventId;
    @Column(name = "NAME")
    private String name;
    @Column(name = "ADDRESS")
    private String address;
    @OneToMany(mappedBy = "place")
    private List<Section> sections = new ArrayList<>();
}