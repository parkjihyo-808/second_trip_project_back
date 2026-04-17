package com.busanit401.second_trip_project_back.controller;
import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.dto.loging.ReservationRequestDTO;
import com.busanit401.second_trip_project_back.dto.loging.ReservationResponseDTO;
import com.busanit401.second_trip_project_back.enums.ReservationStatus;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.service.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
@Slf4j
public class ReservationController {

    private final ReservationService reservationService;
    private final MemberRepository memberRepository;

    // ─── 예약 생성 ────────────────────────────────────
    // POST /api/reservations
    @PostMapping
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ReservationRequestDTO requestDTO) {

        Member member = getMember(userDetails);
        ReservationResponseDTO response =
                reservationService.createReservation(member, requestDTO);
        return ResponseEntity.ok(response);
    }

    // ─── 내 예약 목록 조회 ────────────────────────────
    // GET /api/reservations
    @GetMapping
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservations(
            @AuthenticationPrincipal UserDetails userDetails) {

        Member member = getMember(userDetails);
        List<ReservationResponseDTO> response =
                reservationService.getMyReservations(member);
        return ResponseEntity.ok(response);
    }

    // ─── 예약 상태별 조회 ─────────────────────────────
    // GET /api/reservations/status?status=CONFIRMED
    @GetMapping("/status")
    public ResponseEntity<List<ReservationResponseDTO>> getMyReservationsByStatus(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam ReservationStatus status) {

        Member member = getMember(userDetails);
        List<ReservationResponseDTO> response =
                reservationService.getMyReservationsByStatus(member, status);
        return ResponseEntity.ok(response);
    }

    // ─── 예약 취소 ────────────────────────────────────
    // DELETE /api/reservations/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<ReservationResponseDTO> cancelReservation(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        Member member = getMember(userDetails);
        ReservationResponseDTO response =
                reservationService.cancelReservation(member, id);
        return ResponseEntity.ok(response);
    }

    // ─── 회원 조회 헬퍼 ──────────────────────────────
    private Member getMember(UserDetails userDetails) {
        return memberRepository.findByMid(userDetails.getUsername())
                .orElseThrow(() ->
                        new IllegalArgumentException("회원을 찾을 수 없습니다."));
    }
}
