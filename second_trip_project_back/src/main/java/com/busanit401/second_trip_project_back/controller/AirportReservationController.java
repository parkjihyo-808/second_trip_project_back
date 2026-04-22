package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.airport.AirportReservationDTO;
import com.busanit401.second_trip_project_back.service.airport.AirportReservationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

// 항공 예약 REST API 컨트롤러
// Swagger: http://서버IP:8080/swagger-ui/index.html 에서 확인 가능
@RestController
@RequestMapping("/api/airport")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "AirportReservation", description = "항공 예약 API")
public class AirportReservationController {

    private final AirportReservationService airportReservationService;

    // ── 예약 등록 ─────────────────────────────────────────────
    // POST /api/airport/reservations
    // Flutter ReservationController.addReservation() 에서 호출
    // 중복 예약 시 RuntimeException → 400 + message 반환
    // 성공 시 200 + 생성된 예약 id 반환
    @PostMapping("/reservations")
    @Operation(summary = "예약 등록",
            description = "항공권 예약 등록 (중복 예약 시 400 반환)")
    public ResponseEntity<?> register(
            @RequestBody AirportReservationDTO dto) {

        log.info("✅ [AirportReservationController] 예약 등록 시작 → mid: {}", dto.getMid());
        log.info("✅ [AirportReservationController] depAirportId: {} / arrAirportId: {} / depPlandTime: {}",
                dto.getDepAirportId(), dto.getArrAirportId(), dto.getDepPlandTime());

        try {
            Long id = airportReservationService.register(dto);
            log.info("✅ [AirportReservationController] 예약 등록 완료 → id: {}", id);
            return ResponseEntity.ok(id);
        } catch (RuntimeException e) {
            // 중복 예약 또는 기타 오류 → 400 + 에러 메시지 반환
            // Flutter 에서 error body['message'] 로 파싱
            log.warn("❌ [AirportReservationController] 예약 실패 → {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        }
    }

    // ── 예약 단건 조회 ────────────────────────────────────────
    // GET /api/airport/reservations/{id}
    // 현재 Flutter 에서 미사용
//    @GetMapping("/reservations/{id}")
//    @Operation(summary = "예약 단건 조회",
//            description = "예약 ID로 단건 조회")
//    public ResponseEntity<AirportReservationDTO> getReservation(
//            @PathVariable Long id) {
//
//        log.info("✅ [AirportReservationController] 예약 단건 조회 → id: {}", id);
//
//        AirportReservationDTO dto = airportReservationService.getReservation(id);
//
//        return ResponseEntity.ok(dto);
//    }

    // ── 전체 예약 목록 조회 ───────────────────────────────────
    // GET /api/airport/reservations
    // 관리자 기능 (현재 Flutter 에서 미사용)
//    @GetMapping("/reservations")
//    @Operation(summary = "전체 예약 목록 조회",
//            description = "전체 예약 목록 조회 (관리자)")
//    public ResponseEntity<List<AirportReservationDTO>> getReservationList() {
//
//        log.info("✅ [AirportReservationController] 전체 예약 목록 조회");
//
//        List<AirportReservationDTO> list =
//                airportReservationService.getReservationList();
//
//        log.info("✅ [AirportReservationController] 조회 완료 → {}건", list.size());
//
//        return ResponseEntity.ok(list);
//    }

    // ── 회원 ID(mid)로 예약 목록 조회 ────────────────────────
    // GET /api/airport/reservations/my?mid={mid}
    // Flutter ReservationController.fetchReservations(mid) 에서 호출
    // 정렬: 예약완료(미래 항공편) 먼저, 지난예약(과거 항공편) 나중
    @GetMapping("/reservations/my")
    @Operation(summary = "회원 ID(mid)로 예약 조회",
            description = "로그인 회원 mid로 내 예약 목록 조회")
    public ResponseEntity<List<AirportReservationDTO>> getReservationListByMid(
            @RequestParam String mid) {

        log.info("✅ [AirportReservationController] mid로 예약 조회 → {}", mid);

        List<AirportReservationDTO> list =
                airportReservationService.getReservationListByMid(mid);

        log.info("✅ [AirportReservationController] 조회 완료 → {}건", list.size());

        return ResponseEntity.ok(list);
    }

    // ── 예약 취소 (삭제) ──────────────────────────────────────
    // DELETE /api/airport/reservations/{id}
    // Flutter ReservationController.cancelReservation() 에서 호출
    // CascadeType.ALL 로 탑승객(AirportPassenger) 도 함께 삭제됨
    @DeleteMapping("/reservations/{id}")
    @Operation(summary = "예약 취소",
            description = "예약 취소 (삭제)")
    public ResponseEntity<Void> remove(
            @PathVariable Long id) {

        log.info("✅ [AirportReservationController] 예약 취소 → id: {}", id);

        airportReservationService.remove(id);

        log.info("✅ [AirportReservationController] 예약 취소 완료 → id: {}", id);

        return ResponseEntity.ok().build();
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.controller.AirportReservationController
 * 역할  : 항공 예약 REST API 엔드포인트 제공
 * 사용처 : Flutter ReservationController (예약 등록/조회/취소)
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservationService.java      : 비즈니스 로직 위임
 * - AirportReservationDTO.java          : 요청/응답 DTO
 * - reservation_controller.dart         : Flutter 에서 호출하는 클라이언트
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 CRUD API 구성, 탑승객 이름으로 조회 포함
 * - 변경       : 탑승객 이름 조회 제거 (단일 탑승객 시절 코드)
 *               중복 예약 시 400 + message 반환 추가
 *               getReservationListByMid() 정렬 변경 (findByMidSorted 적용)
 * ----------------------------------------------------------------------------------
 * [API 목록]
 * - POST   /api/airport/reservations          : 예약 등록 (Flutter 메인 사용)
 * - GET    /api/airport/reservations/{id}     : 예약 단건 조회 (현재 미사용)
 * - GET    /api/airport/reservations          : 전체 예약 조회 (관리자/현재 미사용)
 * - GET    /api/airport/reservations/my       : 회원 예약 목록 조회 (Flutter 메인 사용)
 * - DELETE /api/airport/reservations/{id}     : 예약 취소 (Flutter 메인 사용)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - register() 의 ResponseEntity<?> 는 성공(Long) / 실패(Map) 두 가지 타입 반환
 *   Flutter 에서 statusCode 200 → id / 400 → body['message'] 로 파싱
 * - Swagger UI: http://서버IP:8080/swagger-ui/index.html
 * ==================================================================================
 */