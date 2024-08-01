package com.T82.ticket.global.domain.entity;

import com.T82.ticket.dto.request.SectionInitRequestDto;
import jakarta.persistence.*;
import lombok.*;

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
    @Column(name = "REST_SEAT")@Setter
    private Integer restSeat;
    @Column(name = "PRICE")
    private Integer price;
    @Column(name = "START_ROW")
    private Integer startRow;
    @Column(name = "START_COL")
    private Integer startCol;
    @Column(name = "ROW_NUM")
    private Integer rowNum;
    @Column(name = "COL_NUM")
    private Integer colNum;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "EVENT_ID")
    private Place place;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Seat> seats = new ArrayList<>();

    public static Section toEntity(SectionInitRequestDto req, Place place){
        return Section.builder()
                .sectionId(null)
                .name(req.sectionName())
                .startRow(req.startRow())
                .startCol(req.startCol())
                .rowNum(req.rowNum())
                .colNum(req.colNum())
                .price(req.price())
                .restSeat(req.sectionTotalSeat())
                .place(place)
                .build();
    }
    public static void DecreaseInSectionSeats(Section section){
        section.setRestSeat(section.getRestSeat() - 1);
    }

    public static void IncreaseInSectionSeats(Section section){
        section.setRestSeat(section.getRestSeat() + 1);
    }

}
