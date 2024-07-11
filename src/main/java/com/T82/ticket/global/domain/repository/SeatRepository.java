package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
}
