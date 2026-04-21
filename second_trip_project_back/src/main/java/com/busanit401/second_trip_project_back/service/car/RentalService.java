package com.busanit401.second_trip_project_back.service.car;

import com.busanit401.second_trip_project_back.dto.car.CarReservationCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationRequestDTO;

import java.time.LocalDateTime;

public interface RentalService {
    CarReservationDTO createReservation(String mid, CarReservationRequestDTO request);
    CarReservationCursorResponseDTO getMyReservationsCursor(String mid, int cursorStatusOrder, LocalDateTime cursorEndDate, Long cursorId, int size);
    CarReservationDTO cancelReservation(String mid, Long rentalId);

}