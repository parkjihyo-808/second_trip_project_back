package com.busanit401.second_trip_project_back.entity.airport;

import jakarta.persistence.*;
import lombok.*;

// 항공편 엔티티 - DB 테이블명: trip_airport_flight
@Entity
@Table(name = "trip_airport_flight")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AirportFlight {

    // ── 기본키 ──────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── 항공편 정보 ──────────────────────────────────────────
    @Column(nullable = false, length = 50)
    private String airlineNm;    // 항공사명 (예: 대한항공, 제주항공)

    @Column(nullable = false, length = 20)
    private String flightNo;     // 항공편명 (예: KE1234)

    @Column(nullable = false, length = 20)
    private String depAirportId; // 출발 공항코드 (예: GIMHAE)

    @Column(nullable = false, length = 20)
    private String arrAirportId; // 도착 공항코드 (예: JEJU)

    @Column(nullable = false, length = 50)
    private String depAirportNm; // 출발 공항명 (예: 김해(부산))

    @Column(nullable = false, length = 50)
    private String arrAirportNm; // 도착 공항명 (예: 제주)

    @Column(nullable = false)
    private String depPlandTime; // 출발 예정시각 (예: 20260501134000)

    @Column(nullable = false)
    private String arrPlandTime; // 도착 예정시각 (예: 20260501144500)

    // ── 가격 / 잔여석 ────────────────────────────────────────
    // price: TAGO API 원래 필드명은 economyCharge → DB 저장 시 price 로 통일
    @Column(nullable = false)
    private Integer price;       // 일반석 가격

    @Column(nullable = false)
    private Integer seatsLeft;   // 잔여석 (예약 시 차감됨)

    // ── 수정 메서드 ──────────────────────────────────────────
    // 항공편 정보 일괄 수정 시 사용 (관리자 기능 또는 배치 업데이트)
    public void changeFlightInfo(
            String airlineNm,
            String flightNo,
            String depPlandTime,
            String arrPlandTime,
            Integer price,
            Integer seatsLeft) {
        this.airlineNm    = airlineNm;
        this.flightNo     = flightNo;
        this.depPlandTime = depPlandTime;
        this.arrPlandTime = arrPlandTime;
        this.price        = price;
        this.seatsLeft    = seatsLeft;
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.entity.airport.AirportFlight
 * 역할  : 항공편 데이터 엔티티 (DB 테이블: trip_airport_flight)
 * 사용처 : AirportFlightRepository, AirportFlightServiceImpl, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlightDTO.java          : 엔티티 ↔ DTO 변환
 * - AirportFlightRepository.java   : JPA 쿼리
 * - AirportFlightServiceImpl.java  : 비즈니스 로직
 * - AirportReservationServiceImpl  : 예약 시 seatsLeft 차감
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : TAGO API 기준 필드 구성 (economyCharge, vihicleId 등)
 * - 변경       : 필드명 Flutter 연동 기준으로 통일
 *               economyCharge → price
 *               vihicleId → flightNo
 *               공항코드 TAGO 형식 → 영문 코드 (GIMHAE, JEJU 등)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - seatsLeft 는 예약 등록 시 AirportReservationServiceImpl 에서 차감
 * - depPlandTime / arrPlandTime 형식: YYYYMMDDHHMMSS (14자리 문자열)
 * - 테이블명(trip_airport_flight) 과 클래스명(AirportFlight) 이 다름에 주의
 * ==================================================================================
 */