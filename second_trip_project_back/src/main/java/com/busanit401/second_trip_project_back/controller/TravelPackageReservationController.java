package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.TravelPackageReservationDTO;
import com.busanit401.second_trip_project_back.service.TravelPackageReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/package_reservations")
@RequiredArgsConstructor
public class TravelPackageReservationController {

    private final TravelPackageReservationService reservationService;

    // 1. 패키지 예약 등록 (성공했던 로직)
    @PostMapping("")
    public ResponseEntity<Long> createReservation(
            @Valid @RequestBody TravelPackageReservationDTO reservationDTO,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // userDetails.getUsername()을 통해 로그인한 사용자의 ID(email 등)를 가져옴
        Long reservationId = reservationService.register(reservationDTO, userDetails.getUsername());
        return ResponseEntity.ok(reservationId);
    }

    // 2. 내 예약 목록 조회 (현재 404 원인으로 의심되는 부분)
    @GetMapping("/my_package") // ✅ 프론트엔드 ApiClient 주소와 반드시 일치해야 함
    public ResponseEntity<List<TravelPackageReservationDTO>> getMyPackageReservations(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // 로그인한 사용자의 예약 리스트를 서비스에서 호출
        List<TravelPackageReservationDTO> list = reservationService.getMyReservations(userDetails.getUsername());
        return ResponseEntity.ok(list);
    }

    // 3. 예약 취소
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> deleteReservation(@PathVariable("reservationId") Long reservationId) {
        reservationService.remove(reservationId);
        return ResponseEntity.noContent().build();
    }
}