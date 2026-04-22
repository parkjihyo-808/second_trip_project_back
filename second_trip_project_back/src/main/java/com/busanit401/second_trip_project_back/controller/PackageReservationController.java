package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;
import com.busanit401.second_trip_project_back.service.PackageReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/package-reservations")
@RequiredArgsConstructor
@Log4j2
public class PackageReservationController {

    private final PackageReservationService packageReservationService;

    @PostMapping("/")
    public ResponseEntity<?> register(
            @Valid @RequestBody PackageReservationDTO packageReservationDto,
            Principal principal) {

        if (principal == null) {
            log.error("❌ 예약 실패: 인증되지 않은 사용자");
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        log.info("📢 패키지 예약 요청: {} (사용자: {})", packageReservationDto, principal.getName());

        Long reservationId = packageReservationService.register(packageReservationDto, principal.getName());
        return ResponseEntity.ok(reservationId);
    }

    @GetMapping("/my-package")
    public ResponseEntity<?> getMyPackageReservations(Principal principal) {
        if (principal == null) {
            log.error("❌ 예약 목록 조회 실패: 인증되지 않은 사용자");
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        log.info("📢 패키지 예약 목록 조회 요청: {}", principal.getName());

        List<PackageReservationDTO> list = packageReservationService.getMyPackageReservations(principal.getName());
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<String> delete(@PathVariable Long reservationId, Principal principal) {

        log.info("📢 패키지 예약 삭제 요청: ID={} (사용자: {})", reservationId, principal.getName());

        packageReservationService.deleteReservation(reservationId, principal.getName());

        return ResponseEntity.ok("예약이 삭제되었습니다.");
    }
}