package com.busanit401.second_trip_project_back.dto.car;

import lombok.Data;

import java.time.LocalDate;

@Data
public class CarSearchCursorRequestDTO {
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
    private int cursorPrice = 0;
    private String cursorName = "";
    private int size = 10;
}