package com.busanit401.second_trip_project_back.dto.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportFlight;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

// 항공편 DTO - Entity ↔ Flutter 간 데이터 전달 객체
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirportFlightDTO {

    // ── 항공편 정보 ──────────────────────────────────────────
    private Long   id;
    private String airlineNm;      // 항공사명
    private String flightNo;       // 항공편명 (예: KE1234)
    private String depAirportId;   // 출발 공항코드 (예: GIMHAE)
    private String arrAirportId;   // 도착 공항코드 (예: JEJU)
    private String depAirportNm;   // 출발 공항명
    private String arrAirportNm;   // 도착 공항명
    private String depPlandTime;   // 출발 예정시각 (예: 20260501134000)
    private String arrPlandTime;   // 도착 예정시각

    // ⚠️ [주의] 필드명 불일치
    // Entity: price / DTO: economyCharge / Flutter: json['economyCharge']
    // 현재 방법1 유지 (작동 정상) → 발표 후 price 로 통일 예정
    private Integer economyCharge; // 일반석 가격 (Entity.price 와 매핑)

    private Integer seatsLeft;     // 잔여석

    // ── Entity → DTO 변환 ────────────────────────────────────
    // 서버 → Flutter 응답 시 사용
    // entity.getPrice() → dto.economyCharge 로 매핑
    public static AirportFlightDTO fromEntity(AirportFlight entity) {
        return AirportFlightDTO.builder()
                .id(entity.getId())
                .airlineNm(entity.getAirlineNm())
                .flightNo(entity.getFlightNo())
                .depAirportId(entity.getDepAirportId())
                .arrAirportId(entity.getArrAirportId())
                .depAirportNm(entity.getDepAirportNm())
                .arrAirportNm(entity.getArrAirportNm())
                .depPlandTime(entity.getDepPlandTime())
                .arrPlandTime(entity.getArrPlandTime())
                .economyCharge(entity.getPrice()) // price → economyCharge 매핑
                .seatsLeft(entity.getSeatsLeft())
                .build();
    }

    // ── DTO → Entity 변환 ────────────────────────────────────
    // Flutter → 서버 등록/수정 요청 시 사용
    // dto.economyCharge → entity.price 로 매핑
    public AirportFlight toEntity() {
        return AirportFlight.builder()
                .airlineNm(airlineNm)
                .flightNo(flightNo)
                .depAirportId(depAirportId)
                .arrAirportId(arrAirportId)
                .depAirportNm(depAirportNm)
                .arrAirportNm(arrAirportNm)
                .depPlandTime(depPlandTime)
                .arrPlandTime(arrPlandTime)
                .price(economyCharge) // economyCharge → price 매핑
                .seatsLeft(seatsLeft)
                .build();
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.dto.airport.AirportFlightDTO
 * 역할  : 항공편 데이터 전달 객체 (Entity ↔ Flutter 간 변환)
 * 사용처 : AirportFlightController, AirportFlightServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlight.java             : 변환 대상 엔티티
 * - AirportFlightServiceImpl.java  : fromEntity() / toEntity() 호출
 * - AirportFlightController.java   : 요청/응답 DTO 로 사용
 * - flight_item.dart               : Flutter 에서 JSON 파싱 기준
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : TAGO API 필드명 기준 (economyCharge, vihicleId 등)
 * - 변경       : flightNo 필드 추가 (vihicleId 대체)
 *               fromEntity / toEntity 변환 메서드 추가
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ 필드명 불일치 현황 (발표 후 통일 예정)
 *    Entity: price  →  DTO: economyCharge  →  Flutter: json['economyCharge']
 *    현재 fromEntity/toEntity 에서 수동 매핑으로 정상 작동 중
 * ==================================================================================
 */