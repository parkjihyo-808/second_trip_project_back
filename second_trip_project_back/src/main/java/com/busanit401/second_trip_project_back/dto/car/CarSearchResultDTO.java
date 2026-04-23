package com.busanit401.second_trip_project_back.dto.car;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CarSearchResultDTO {

    private String carName;
    private String type;
    private int seats;
    private String fuel;
    private int lowestPrice;
    private List<CompanyCarDTO> companyCarDTOs;

    @Data
    @Builder
    public static class CompanyCarDTO {
        private Long carId;
        private String companyName;
        private int year;
        private int dailyPrice;
    }
}