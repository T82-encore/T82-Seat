package com.T82.ticket.global.domain.repository;


import com.T82.ticket.global.domain.entity.ChoiceSeat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceSeatRepository extends JpaRepository<ChoiceSeat,Long> {
    ChoiceSeat findBySeatId(Long SeatId);

}
