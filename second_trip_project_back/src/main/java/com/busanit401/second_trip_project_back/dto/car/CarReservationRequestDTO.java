package com.busanit401.second_trip_project_back.dto.car;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarReservationRequestDTO {
    private Long carId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}