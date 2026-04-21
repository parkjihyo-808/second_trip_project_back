package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.car.CompanyCarPageResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;
import com.busanit401.second_trip_project_back.service.car.RentCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/car")
@Log4j2
@RequiredArgsConstructor
public class RentCarController {

    private final RentCarService rentCarService;

    // 지역 목록
    // GET /car/regions
    @GetMapping("/regions")
    public ResponseEntity<List<String>> getRegions() {
        return ResponseEntity.ok(rentCarService.getRegions());
    }

    // 차종(커서) + 옵션(페이지) 한 번에
    // GET /car/search/all?region=부산&startDate=2025-05-01&endDate=2025-05-05&cursorPrice=0&cursorName=&size=10&optionSize=3
    @GetMapping("/search/all")
    public ResponseEntity<CarSearchCursorResponseDTO> searchCarsWithOptions(
            @RequestParam String region,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int cursorPrice,
            @RequestParam(defaultValue = "") String cursorName,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "3") int optionSize) {
        return ResponseEntity.ok(rentCarService.searchCarsWithCompanyCars(region, startDate, endDate, cursorPrice, cursorName, size, optionSize));
    }

    // 특정 차량의 업체별 옵션 - 페이지 기반
    // GET /car/search/company?carName=모닝&region=부산&startDate=2025-05-01&endDate=2025-05-05&page=0&size=3
    @GetMapping("/search/company")
    public ResponseEntity<CompanyCarPageResponseDTO> searchCarOptions(
            @RequestParam String carName,
            @RequestParam String region,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "3") int size) {
        return ResponseEntity.ok(rentCarService.searchCompanyCars(carName, region, startDate, endDate, page, size));
    }
}