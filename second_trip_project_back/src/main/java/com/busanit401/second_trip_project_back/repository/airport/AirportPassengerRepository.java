package com.busanit401.second_trip_project_back.repository.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportPassenger;
import org.springframework.data.jpa.repository.JpaRepository;

// 탑승객 레포지토리
// 현재 별도 커스텀 쿼리 없음
// 탑승객 저장/삭제는 AirportReservation CascadeType.ALL 로 자동 처리
public interface AirportPassengerRepository
        extends JpaRepository<AirportPassenger, Long> {
    // 기본 CRUD (save, findById, deleteById 등) JpaRepository 에서 제공
    // 탑승객 개별 조회/삭제가 필요한 경우 쿼리 메서드 추가 예정
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.repository.airport.AirportPassengerRepository
 * 역할  : 탑승객 DB 접근 (JPA Repository)
 * 사용처 : AirportReservationServiceImpl (탑승객 개별 저장 시 직접 호출 가능)
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportPassenger.java              : 조회 대상 엔티티
 * - AirportReservation.java            : CascadeType.ALL 로 탑승객 자동 저장/삭제
 * - AirportReservationServiceImpl.java : 예약 등록 시 탑승객 저장 처리
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 JpaRepository 만 상속 (커스텀 쿼리 없음)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - 탑승객은 AirportReservation CascadeType.ALL 설정으로
 *   예약 저장/삭제 시 자동으로 함께 처리됨
 * - 직접 save() 호출이 필요한 경우 AirportReservationServiceImpl 에서 사용
 * ==================================================================================
 */