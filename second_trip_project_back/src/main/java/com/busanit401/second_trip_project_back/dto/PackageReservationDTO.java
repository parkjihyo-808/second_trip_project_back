package com.busanit401.second_trip_project_back.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PackageReservationDTO {

    private Long packageId;
    private LocalDate reservationDate;
    @Min(1)
    private int peopleCount;
    private int totalPrice;
}