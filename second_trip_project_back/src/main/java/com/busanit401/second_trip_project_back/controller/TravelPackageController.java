package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.TravelPackageItemDTO;
import com.busanit401.second_trip_project_back.service.TravelPackageService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/packages")
@RequiredArgsConstructor
public class TravelPackageController {

    private final TravelPackageService travelPackageService;

    // 1. 전체 상품 조회 (페이징 포함)
    // 호출 예시: /api/packages/list?page=0&size=10&sort=id,desc
    @GetMapping("/packages_list")
    public ResponseEntity<Page<TravelPackageItemDTO>> getList(
            @RequestParam(name = "category") String category,
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {

        return ResponseEntity.ok(travelPackageService.getList(category, pageable));
    }

    // 2. 단건 상세 조회
    // 호출 예시: /api/packages/1001
    @GetMapping("/{id}")
    public ResponseEntity<TravelPackageItemDTO> getOne(@PathVariable("id") Long id) {

        return ResponseEntity.ok(travelPackageService.getOne(id));
    }
}