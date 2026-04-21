package com.busanit401.second_trip_project_back.repository.car;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;
import com.busanit401.second_trip_project_back.repository.car.search.CarReservationSearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarReservationRepository extends JpaRepository<CarReservation, Long>, CarReservationSearch {
}