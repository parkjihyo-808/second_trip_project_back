package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorRequestDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;
import com.busanit401.second_trip_project_back.service.car.RentCarService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    // 차종(커서) + 업체 차량 전체 한 번에
    // GET /car/search/all?region=부산&startDate=2025-05-01&endDate=2025-05-05&cursorPrice=0&cursorName=&size=10
    @GetMapping("/search/all")
    public ResponseEntity<CarSearchCursorResponseDTO> searchCarsWithOptions(
            @ModelAttribute CarSearchCursorRequestDTO request) {
        return ResponseEntity.ok(rentCarService.searchCarsWithCompanyCars(
                request.getRegion(),
                request.getStartDate(),
                request.getEndDate(),
                request.getCursorPrice(),
                request.getCursorName(),
                request.getSize()));
    }

}