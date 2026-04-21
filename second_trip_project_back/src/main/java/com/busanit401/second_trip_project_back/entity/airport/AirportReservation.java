package com.busanit401.second_trip_project_back.entity.airport;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

// 항공 예약 엔티티 - DB 테이블명: trip_airport_reservation
// 가는편 + 오는편(왕복) + 탑승객 목록 통합 관리
@Entity
@Table(name = "trip_airport_reservation")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class AirportReservation {

    // ── 기본키 ──────────────────────────────────────────────
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ── 회원 정보 ────────────────────────────────────────────
    // 로그인 회원 ID (Member 엔티티와 직접 연관관계 없이 mid 문자열만 저장)
    @Column(length = 50)
    private String mid;

    // ── 가는편 정보 ──────────────────────────────────────────
    @Column(nullable = false, length = 50)
    private String airlineNm;       // 항공사명

    @Column(nullable = false, length = 20)
    private String flightNo;        // 항공편명 (예: KE1234)

    @Column(nullable = false, length = 50)
    private String depAirportNm;    // 출발 공항명

    @Column(nullable = false, length = 50)
    private String arrAirportNm;    // 도착 공항명

    @Column(nullable = false, length = 20)
    private String depAirportId;    // 출발 공항코드 (예: GIMHAE)

    @Column(nullable = false, length = 20)
    private String arrAirportId;    // 도착 공항코드 (예: JEJU)

    @Column(nullable = false)
    private String depPlandTime;    // 출발 예정시각 (예: 20260501134000)

    @Column(nullable = false)
    private String arrPlandTime;    // 도착 예정시각

    @Column(nullable = false)
    private Integer depPrice;       // 가는편 가격

    // ── 오는편 정보 (왕복일 때만 값 있음, 편도일 때 null) ────
    @Column(length = 50)
    private String retAirlineNm;    // 오는편 항공사명

    @Column(length = 20)
    private String retFlightNo;     // 오는편 항공편명

    @Column
    private String retDepPlandTime; // 오는편 출발 예정시각

    @Column
    private String retArrPlandTime; // 오는편 도착 예정시각

    @Column
    private Integer retPrice;       // 오는편 가격

    // ── 탑승객 목록 (AirportPassenger 테이블로 분리) ─────────
    // 1:N 관계 - 예약 1건에 탑승객 여러 명
    // CascadeType.ALL: 예약 저장/삭제 시 탑승객도 함께 처리
    // orphanRemoval: 목록에서 제거된 탑승객 자동 삭제
    @OneToMany(mappedBy = "reservation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    @Builder.Default
    private List<AirportPassenger> passengers = new ArrayList<>();

    // ── 예약 메타 정보 ───────────────────────────────────────
    @Column(nullable = false)
    private Boolean isRoundTrip;    // 왕복 여부 (true: 왕복 / false: 편도)

    @Column(nullable = false)
    private String reservedAt;      // 예약 일시 (Flutter 에서 DateTime.now() 전달)

    // ── 수정 메서드 ──────────────────────────────────────────
    // 단일 탑승객 구조에서 사용하던 메서드
    // 탑승객 목록(passengers) 구조로 변경 후 실질적으로 미사용
    // 추후 삭제 또는 탑승객 수정 로직으로 대체 예정
    public void changeReservationInfo(
            String passengerName,
            String passengerBirth,
            String passengerGender) {
        // TODO: 탑승객 수정 로직 필요 시 구현
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.entity.airport.AirportReservation
 * 역할  : 항공 예약 엔티티 (DB 테이블: trip_airport_reservation)
 * 사용처 : AirportReservationRepository, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportPassenger.java              : 자식 엔티티 (1:N 관계)
 * - AirportReservationDTO.java         : 엔티티 ↔ DTO 변환
 * - AirportReservationRepository.java  : JPA 쿼리
 * - AirportReservationServiceImpl.java : 예약 등록/취소/조회 비즈니스 로직
 * - AirportFlight.java                 : 예약 시 seatsLeft 차감 대상
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 필드 구조 (passengerName, passengerBirth, passengerGender)
 * - 변경       : 탑승객 다중 지원 → AirportPassenger 테이블 분리
 *               기존 단일 탑승객 필드 주석 처리 후 List<AirportPassenger> 로 전환
 *               mid 필드 추가 (로그인 회원 연동)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - mid 는 Member 엔티티와 직접 연관관계 없이 문자열로만 저장
 * - 오는편 필드(ret*) 는 편도일 때 null → Flutter 에서 null 허용으로 처리
 * - passengers 는 CascadeType.ALL 로 예약 삭제 시 탑승객도 함께 삭제
 * - changeReservationInfo() 는 현재 미사용 (추후 탑승객 수정 기능 추가 시 활용)
 * ==================================================================================
 */