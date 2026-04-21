package com.busanit401.second_trip_project_back.dto.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportPassenger;
import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import lombok.*;

// 탑승객 DTO - Entity ↔ Flutter 간 데이터 전달 객체
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirportPassengerDTO {

    // ── 기본키 ──────────────────────────────────────────────
    // 조회 시에만 값 있음 / Flutter → 서버 전송 시 id 미포함 (서버 자동 생성)
    private Long id;

    // ── 탑승객 정보 ──────────────────────────────────────────
    private String passengerType;   // 탑승객 유형 (성인 / 소아 / 유아)
    private String passengerName;   // 탑승객 이름 (예: 홍 길동)
    private String passengerBirth;  // 생년월일 YYYYMMDD (예: 19990101)
    private String passengerGender; // 성별 (남성 / 여성)

    // ── Entity → DTO 변환 ────────────────────────────────────
    // 서버 → Flutter 응답 시 사용 (예약 조회 시 탑승객 목록 반환)
    public static AirportPassengerDTO fromEntity(AirportPassenger entity) {
        return AirportPassengerDTO.builder()
                .id(entity.getId())
                .passengerType(entity.getPassengerType())
                .passengerName(entity.getPassengerName())
                .passengerBirth(entity.getPassengerBirth())
                .passengerGender(entity.getPassengerGender())
                .build();
    }

    // ── DTO → Entity 변환 ────────────────────────────────────
    // Flutter → 서버 예약 등록 시 사용
    // reservation 파라미터: 저장된 AirportReservation 엔티티 (외래키 연결)
    public AirportPassenger toEntity(AirportReservation reservation) {
        return AirportPassenger.builder()
                .reservation(reservation)       // N:1 연관관계 설정
                .passengerType(passengerType)
                .passengerName(passengerName)
                .passengerBirth(passengerBirth)
                .passengerGender(passengerGender)
                .build();
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.dto.airport.AirportPassengerDTO
 * 역할  : 탑승객 데이터 전달 객체 (Entity ↔ Flutter 간 변환)
 * 사용처 : AirportReservationDTO, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportPassenger.java              : 변환 대상 엔티티
 * - AirportReservation.java            : toEntity() 에서 외래키 연결에 사용
 * - AirportReservationDTO.java         : 예약 DTO 내부에서 passengers 리스트로 포함
 * - AirportReservationServiceImpl.java : toEntity(reservation) 호출
 * - passenger_item.dart                : Flutter 에서 JSON 파싱 기준
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 구조
 * - 변경       : 예약 DTO 에 포함되는 리스트 구조로 전환
 *               toEntity() 에 AirportReservation 파라미터 추가 (외래키 연결)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - Flutter PassengerItem.toJson() 에서 id 미포함 전송 → 서버에서 자동 생성
 * - toEntity() 호출 시 반드시 저장 완료된 AirportReservation 전달해야 함
 *   (reservation.getId() 가 null 이면 외래키 오류 발생)
 * ==================================================================================
 */