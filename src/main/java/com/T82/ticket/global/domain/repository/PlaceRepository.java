package com.T82.ticket.global.domain.repository;

import com.T82.ticket.global.domain.entity.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {
}
