package com.busanit401.second_trip_project_back.dto.car;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CarTypeDTO {
    private String carName;
    private String type;
    private int seats;
    private String fuel;
    private int lowestPrice;
}