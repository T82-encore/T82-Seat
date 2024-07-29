package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.Seat;
import com.T82.ticket.global.domain.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("select s from Section s where s.place.eventId= :eventId")
    List<Section> findAllByEventId(@Param("eventId")Long evnetId);

    @Query("SELECT s FROM Seat s JOIN FETCH s.section sec JOIN FETCH sec.place p WHERE p.eventId = :eventId AND s.isBooked = false")
    List<Seat> findAllSeatsByEventId(@Param("eventId") Long eventId);
}
