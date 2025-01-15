package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.SeatReservation;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SeatReservationRepository extends CrudRepository<SeatReservation, Long> {

    List<SeatReservation> findByEventId(Long eventId);
}
