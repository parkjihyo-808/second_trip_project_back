package com.busanit401.second_trip_project_back.service.car;

import com.busanit401.second_trip_project_back.dto.car.CompanyCarPageResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;

import java.time.LocalDate;
import java.util.List;

public interface RentCarService {

    // 지역 목록
    List<String> getRegions();
    CompanyCarPageResponseDTO searchCompanyCars(String carName, String region, LocalDate startDate, LocalDate endDate, int page, int size);
    CarSearchCursorResponseDTO searchCarsWithCompanyCars(String region, LocalDate startDate, LocalDate endDate, int cursorPrice, String cursorName, int size, int companyCarSize);
}