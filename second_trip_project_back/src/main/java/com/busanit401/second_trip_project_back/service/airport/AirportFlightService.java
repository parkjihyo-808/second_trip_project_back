package com.busanit401.second_trip_project_back.service.airport;

import com.busanit401.second_trip_project_back.dto.airport.AirportFlightDTO;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// 항공편 서비스 인터페이스
// 구현체: AirportFlightServiceImpl
@Transactional
public interface AirportFlightService {

    // ── 항공편 목록 조회 ─────────────────────────────────────
    // Flutter 검색 시 호출 (출발/도착 공항코드 + 날짜 앞 8자리)
    List<AirportFlightDTO> getFlightList(
            String depAirportId,
            String arrAirportId,
            String depPlandTime
    );

    // ── 항공편 단건 조회 ─────────────────────────────────────
    // id 로 항공편 단건 조회
    AirportFlightDTO getFlight(Long id);

    // ── 항공편 등록 (관리자) ──────────────────────────────────
    // 새 항공편 등록 후 생성된 id 반환
    Long register(AirportFlightDTO dto);

    // ── 항공편 수정 (관리자) ──────────────────────────────────
    // 항공편 정보 수정 (가격, 잔여석 등)
    void modify(AirportFlightDTO dto);

    // ── 항공편 삭제 (관리자) ──────────────────────────────────
    // id 로 항공편 삭제
    void remove(Long id);
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.service.airport.AirportFlightService
 * 역할  : 항공편 서비스 인터페이스 (메서드 명세 정의)
 * 사용처 : AirportFlightController, AirportFlightServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlightServiceImpl.java  : 구현체
 * - AirportFlightController.java   : 주입받아 사용
 * - AirportFlightDTO.java          : 입출력 DTO
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 CRUD 메서드 정의
 * ----------------------------------------------------------------------------------
 * [메서드 목록]
 * - getFlightList(depAirportId, arrAirportId, depPlandTime) : 항공편 목록 조회
 * - getFlight(id)      : 항공편 단건 조회
 * - register(dto)      : 항공편 등록 (관리자)
 * - modify(dto)        : 항공편 수정 (관리자)
 * - remove(id)         : 항공편 삭제 (관리자)
 * ==================================================================================
 */