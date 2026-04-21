package com.busanit401.second_trip_project_back.repository.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportFlight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

// 항공편 레포지토리 - JPA 쿼리 메서드로 항공편 조회
@Repository
public interface AirportFlightRepository extends JpaRepository<AirportFlight, Long> {

    // ── 출발/도착 공항코드로 항공편 조회 ─────────────────────
    // 날짜 조건 없이 해당 노선 전체 조회 (현재 미사용, 추후 활용 가능)
    List<AirportFlight> findByDepAirportIdAndArrAirportId(
            String depAirportId,
            String arrAirportId
    );

    // ── 출발/도착 공항코드 + 출발날짜로 항공편 조회 ───────────
    // Flutter 검색 시 메인으로 사용하는 쿼리
    // depPlandTime: 앞 8자리(YYYYMMDD) 로 StartingWith 검색
    // 예) "20260501" → 20260501060000 ~ 20260501235959 전체 조회
    List<AirportFlight> findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
            String depAirportId,
            String arrAirportId,
            String depPlandTime
    );
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.repository.airport.AirportFlightRepository
 * 역할  : 항공편 DB 조회 (JPA Repository)
 * 사용처 : AirportFlightServiceImpl, AirportReservationServiceImpl (seatsLeft 차감)
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlight.java             : 조회 대상 엔티티
 * - AirportFlightServiceImpl.java  : getFlightList() 에서 호출
 * - AirportReservationServiceImpl  : 예약 시 항공편 조회 후 seatsLeft 차감
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : findByDepAirportIdAndArrAirportId (날짜 없는 조회)
 * - 변경       : 날짜 포함 쿼리 추가 (StartingWith 방식으로 날짜 앞 8자리 검색)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - depPlandTime 은 14자리 문자열 (YYYYMMDDHHMMSS) 이지만
 *   검색 시 앞 8자리(YYYYMMDD) 만 전달 → StartingWith 로 해당 날짜 전체 조회
 * - findByDepAirportIdAndArrAirportId() 는 현재 미사용 상태
 * ==================================================================================
 */