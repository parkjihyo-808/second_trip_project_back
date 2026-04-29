package com.busanit401.second_trip_project_back.controller.tour;

import com.busanit401.second_trip_project_back.dto.tour.TourItemResponseDTO;
import com.busanit401.second_trip_project_back.service.tour.TourItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tours")
@RequiredArgsConstructor
public class TourController {

    private final TourItemService tourItemService;

    // 1. 전체 리스트 조회
    @GetMapping
    public Page<TourItemResponseDTO> getList(@PageableDefault(size = 10, sort = "tourId") Pageable pageable) {
        return tourItemService.getList(pageable);
    }

    // 2. 상세 조회
    @GetMapping("/{id}")
    public TourItemResponseDTO getOne(@PathVariable("id") Long id) {
        return tourItemService.getOne(id);
    }
}
