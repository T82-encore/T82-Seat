package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SectionRepository extends JpaRepository<Section, Long> {
    @Query("select s from Section s where s.place.eventId= :eventId")
    List<Section> findAllByEventId(@Param("eventId")Long evnetId);
}
