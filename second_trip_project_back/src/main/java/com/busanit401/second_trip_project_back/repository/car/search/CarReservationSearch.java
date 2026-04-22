package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;

import java.time.LocalDateTime;
import java.util.List;

public interface CarReservationSearch {

    List<CarReservation> searchByUserMidWithCursor(String mid, int cursorStatusOrder, LocalDateTime cursorStartDate, Long cursorId, int size);

    boolean searchExistsOverlap(Long carId, LocalDateTime startDate, LocalDateTime endDate);

    boolean searchExistsUserOverlap(String mid, LocalDateTime startDate, LocalDateTime endDate);

    List<Long> searchUnavailableCarIds(LocalDateTime startDate, LocalDateTime endDate);
}