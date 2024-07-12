package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    @Query("select s from Seat s where s.section.sectionId= :sectionId and s.isBooked= false and s.isChoicing= false")
    List<Seat> findAllBySectionId(@Param("sectionId")Long sectionId);
}
