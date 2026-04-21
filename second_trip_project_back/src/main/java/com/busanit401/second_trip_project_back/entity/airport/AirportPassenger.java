package com.busanit401.second_trip_project_back.entity.airport;

import jakarta.persistence.*;
import lombok.*;

// 탑승객 엔티티 - DB 테이블명: trip_airport_passenger
// AirportReservation 과 N:1 관계 (예약 1건 → 탑승객 여러 명)
@Entity
@Table(name = "trip_airport_passenger")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AirportPassenger {

    // ── 기본키 ──────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── 예약과 연관관계 (N:1) ────────────────────────────────
    // 지연 로딩(LAZY): 탑승객 조회 시 예약 정보 즉시 로딩 방지
    // reservation_id: 외래키 (trip_airport_reservation.id 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private AirportReservation reservation;

    // ── 탑승객 정보 ──────────────────────────────────────────
    @Column(nullable = false, length = 10)
    private String passengerType;   // 탑승객 유형 (성인 / 소아 / 유아)

    @Column(nullable = false, length = 50)
    private String passengerName;   // 이름 (예: 홍 길동)

    @Column(nullable = false, length = 8)
    private String passengerBirth;  // 생년월일 YYYYMMDD (예: 19990101)

    @Column(nullable = false, length = 10)
    private String passengerGender; // 성별 (남성 / 여성)
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.entity.airport.AirportPassenger
 * 역할  : 탑승객 데이터 엔티티 (DB 테이블: trip_airport_passenger)
 * 사용처 : AirportPassengerRepository, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservation.java          : 부모 엔티티 (1:N 관계)
 * - AirportPassengerDTO.java         : 엔티티 ↔ DTO 변환
 * - AirportPassengerRepository.java  : JPA 쿼리
 * - AirportReservationServiceImpl    : 예약 등록 시 탑승객 저장
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 구조
 * - 변경       : Flutter 다중 탑승객 지원으로 N:1 연관관계 구조로 확장
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - AirportReservation 과 N:1 관계 (예약 1건에 탑승객 여러 명)
 * - fetch = LAZY: 탑승객 목록 조회 시 예약 정보 불필요하므로 지연 로딩
 * - passengerBirth 형식: YYYYMMDD (8자리 문자열)
 * - Flutter PassengerItem.toJson() 에서 id 미포함 전송 → 서버에서 자동 생성
 * ==================================================================================
 */