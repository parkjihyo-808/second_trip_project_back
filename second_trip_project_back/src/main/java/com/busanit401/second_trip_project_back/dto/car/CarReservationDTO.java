package com.busanit401.second_trip_project_back.dto.car;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Data
@Builder
public class CarReservationDTO {
    private Long id;
    private Long carId;
    private String carName;
    private String companyName;
    private String userId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private int totalPrice;
    private LocalDateTime createdAt;

    public static CarReservationDTO from(CarReservation carReservation) {
        long hours = ChronoUnit.HOURS.between(carReservation.getStartDate(), carReservation.getEndDate());
        int days = Math.max(1, (int) Math.ceil(hours / 24.0));
        return CarReservationDTO.builder()
                .id(carReservation.getId())
                .carId(carReservation.getCar().getId())
                .carName(carReservation.getCar().getName())
                .companyName(carReservation.getCar().getCompany().getName())
                .userId(carReservation.getUser().getMid())
                .startDate(carReservation.getStartDate())
                .endDate(carReservation.getEndDate())
                .status(carReservation.getStatus().name())
                .totalPrice(carReservation.getCar().getDailyPrice() * days)
                .createdAt(carReservation.getCreatedAt())
                .build();
    }
}