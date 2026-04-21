package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.airport.AirportFlightDTO;
import com.busanit401.second_trip_project_back.service.airport.AirportFlightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// 항공편 REST API 컨트롤러
// Swagger: http://서버IP:8080/swagger-ui/index.html 에서 확인 가능
@RestController
@RequestMapping("/api/airport")
@RequiredArgsConstructor
@Log4j2
@Tag(name = "Airport", description = "항공편 API")
public class AirportFlightController {

    private final AirportFlightService airportFlightService;

    // ── 항공편 목록 조회 ─────────────────────────────────────
    // GET /api/airport/flights?depAirportId=GIMHAE&arrAirportId=JEJU&depPlandTime=20260421
    // Flutter FlightController.fetchInitial() / fetchReturnFlights() 에서 호출
    @GetMapping("/flights")
    @Operation(summary = "항공편 목록 조회",
            description = "출발지/도착지/날짜로 항공편 목록 조회")
    public ResponseEntity<List<AirportFlightDTO>> getFlightList(
            @RequestParam String depAirportId,
            @RequestParam String arrAirportId,
            @RequestParam String depPlandTime) {

        log.info("✅ [AirportFlightController] 항공편 목록 조회 → " +
                        "출발: {} / 도착: {} / 날짜: {}",
                depAirportId, arrAirportId, depPlandTime);

        List<AirportFlightDTO> list = airportFlightService.getFlightList(
                depAirportId, arrAirportId, depPlandTime);

        log.info("✅ [AirportFlightController] 조회 결과: {}건", list.size());

        return ResponseEntity.ok(list);
    }

    // ── 항공편 단건 조회 ─────────────────────────────────────
    // GET /api/airport/flights/{id}
    // 현재 Flutter 에서 미사용
    @GetMapping("/flights/{id}")
    @Operation(summary = "항공편 단건 조회",
            description = "항공편 ID로 단건 조회")
    public ResponseEntity<AirportFlightDTO> getFlight(
            @PathVariable Long id) {

        log.info("✅ [AirportFlightController] 항공편 단건 조회 → id: {}", id);

        AirportFlightDTO dto = airportFlightService.getFlight(id);

        return ResponseEntity.ok(dto);
    }

    // ── 항공편 등록 (관리자) ──────────────────────────────────
    // POST /api/airport/flights
    // 관리자 기능 (Flutter 에서 직접 호출하지 않음)
    @PostMapping("/flights")
    @Operation(summary = "항공편 등록 (관리자)",
            description = "새로운 항공편 등록")
    public ResponseEntity<Long> register(
            @RequestBody AirportFlightDTO dto) {

        log.info("✅ [AirportFlightController] 항공편 등록 → {}", dto);

        Long id = airportFlightService.register(dto);

        log.info("✅ [AirportFlightController] 등록 완료 → id: {}", id);

        return ResponseEntity.ok(id);
    }

    // ── 항공편 수정 (관리자) ──────────────────────────────────
    // PUT /api/airport/flights/{id}
    // PathVariable id 를 DTO 에 직접 설정 후 서비스 호출
    // ⚠️ economyCharge(DTO) → price(Entity) 매핑 (필드명 불일치, 작동 정상)
    @PutMapping("/flights/{id}")
    @Operation(summary = "항공편 수정 (관리자)",
            description = "항공편 정보 수정")
    public ResponseEntity<Void> modify(
            @PathVariable Long id,
            @RequestBody AirportFlightDTO dto) {

        log.info("✅ [AirportFlightController] 항공편 수정 → id: {}", id);

        // PathVariable id 를 DTO 에 세팅 후 서비스 전달
        dto = AirportFlightDTO.builder()
                .id(id)
                .airlineNm(dto.getAirlineNm())
                .flightNo(dto.getFlightNo())
                .depAirportId(dto.getDepAirportId())
                .arrAirportId(dto.getArrAirportId())
                .depAirportNm(dto.getDepAirportNm())
                .arrAirportNm(dto.getArrAirportNm())
                .depPlandTime(dto.getDepPlandTime())
                .arrPlandTime(dto.getArrPlandTime())
                .economyCharge(dto.getEconomyCharge()) // ⚠️ 발표 후 price 로 통일 예정
                .seatsLeft(dto.getSeatsLeft())
                .build();

        airportFlightService.modify(dto);

        log.info("✅ [AirportFlightController] 수정 완료 → id: {}", id);

        return ResponseEntity.ok().build();
    }

    // ── 항공편 삭제 (관리자) ──────────────────────────────────
    // DELETE /api/airport/flights/{id}
    // 관리자 기능 (Flutter 에서 직접 호출하지 않음)
    @DeleteMapping("/flights/{id}")
    @Operation(summary = "항공편 삭제 (관리자)",
            description = "항공편 삭제")
    public ResponseEntity<Void> remove(
            @PathVariable Long id) {

        log.info("✅ [AirportFlightController] 항공편 삭제 → id: {}", id);

        airportFlightService.remove(id);

        log.info("✅ [AirportFlightController] 삭제 완료 → id: {}", id);

        return ResponseEntity.ok().build();
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.controller.AirportFlightController
 * 역할  : 항공편 REST API 엔드포인트 제공
 * 사용처 : Flutter FlightController (항공편 조회), 관리자 (등록/수정/삭제)
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlightService.java      : 비즈니스 로직 위임
 * - AirportFlightDTO.java          : 요청/응답 DTO
 * - flight_controller.dart         : Flutter 에서 호출하는 클라이언트
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 CRUD API 구성
 * ----------------------------------------------------------------------------------
 * [API 목록]
 * - GET    /api/airport/flights                : 항공편 목록 조회 (Flutter 메인 사용)
 * - GET    /api/airport/flights/{id}           : 항공편 단건 조회 (현재 미사용)
 * - POST   /api/airport/flights                : 항공편 등록 (관리자)
 * - PUT    /api/airport/flights/{id}           : 항공편 수정 (관리자)
 * - DELETE /api/airport/flights/{id}           : 항공편 삭제 (관리자)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ 필드명 불일치 (발표 후 통일 예정)
 *    modify() 에서 dto.getEconomyCharge() 사용
 *    Entity: price / DTO: economyCharge / Flutter: json['economyCharge']
 * - Swagger UI: http://서버IP:8080/swagger-ui/index.html
 * ==================================================================================
 */