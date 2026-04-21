package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.car.CarReservationCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationRequestDTO;
import com.busanit401.second_trip_project_back.service.car.RentalService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/car/reservation")
@Log4j2
@RequiredArgsConstructor
public class CarReservationController {

    private final RentalService rentalService;

    // 예약 생성
    // POST /api/car/reservation
    @PostMapping
    public ResponseEntity<?> createRental(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CarReservationRequestDTO request) {
        try {
            return ResponseEntity.ok(rentalService.createReservation(userDetails.getUsername(), request));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    // 내 예약 목록 (커서 기반 - 예약중 우선, endDate ASC)
    // GET /api/car/reservation/my?size=10
    // GET /api/car/reservation/my?cursorStatusOrder=0&cursorEndDate=2025-05-10T00:00:00&cursorId=5&size=10
    @GetMapping("/my")
    public ResponseEntity<CarReservationCursorResponseDTO> getMyRentals(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int cursorStatusOrder,
            @RequestParam(defaultValue = "9999-12-31T23:59:59") LocalDateTime cursorEndDate,
            @RequestParam(defaultValue = "0") Long cursorId,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(rentalService.getMyReservationsCursor(
                userDetails.getUsername(), cursorStatusOrder, cursorEndDate, cursorId, size));
    }

    // 예약 취소
    // DELETE /api/car/reservation/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<CarReservationDTO> cancelRental(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        return ResponseEntity.ok(rentalService.cancelReservation(userDetails.getUsername(), id));
    }
}