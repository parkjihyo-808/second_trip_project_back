package com.busanit401.second_trip_project_back.service.airport;

import com.busanit401.second_trip_project_back.dto.airport.AirportReservationDTO;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

// 항공 예약 서비스 인터페이스
// 구현체: AirportReservationServiceImpl
@Transactional
public interface AirportReservationService {

    // ── 예약 등록 ─────────────────────────────────────────────
    // 중복 체크 → 예약 저장 → 탑승객 저장 → 잔여석 차감
    // 성공 시 생성된 예약 id 반환
    Long register(AirportReservationDTO dto);

    // ── 예약 단건 조회 ────────────────────────────────────────
    // id 로 단건 조회 (현재 미사용)
    AirportReservationDTO getReservation(Long id);

    // ── 전체 예약 목록 조회 ───────────────────────────────────
    // 전체 예약 최신순 조회 (현재 미사용 / 관리자 기능)
    List<AirportReservationDTO> getReservationList();

    // ── 회원 ID로 예약 목록 조회 ─────────────────────────────
    // MyReservationScreen 진입 시 호출
    // 정렬: 예약완료(미래 항공편) 먼저, 지난예약(과거 항공편) 나중
    List<AirportReservationDTO> getReservationListByMid(String mid);

    // ── 예약 취소 (삭제) ──────────────────────────────────────
    // id 로 예약 삭제 (CascadeType.ALL 로 탑승객도 함께 삭제)
    void remove(Long id);
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.service.airport.AirportReservationService
 * 역할  : 항공 예약 서비스 인터페이스 (메서드 명세 정의)
 * 사용처 : AirportReservationController, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservationServiceImpl.java  : 구현체
 * - AirportReservationController.java   : 주입받아 사용
 * - AirportReservationDTO.java          : 입출력 DTO
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 CRUD + 탑승객 이름으로 조회 메서드 포함
 * - 변경       : 단일 탑승객 → 다중 탑승객 구조 전환
 *               getReservationListByMemberId() → getReservationListByMid() 로 변경
 *               getReservationListByName() 제거 (단일 탑승객 시절 코드)
 * ----------------------------------------------------------------------------------
 * [메서드 목록]
 * - register(dto)                : 예약 등록
 * - getReservation(id)           : 예약 단건 조회 (현재 미사용)
 * - getReservationList()         : 전체 예약 조회 (현재 미사용)
 * - getReservationListByMid(mid) : 회원 예약 목록 조회 (정렬 적용)
 * - remove(id)                   : 예약 취소
 * ==================================================================================
 */