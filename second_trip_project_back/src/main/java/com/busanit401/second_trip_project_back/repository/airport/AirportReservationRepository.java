package com.busanit401.second_trip_project_back.repository.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

// 항공 예약 레포지토리
@Repository
public interface AirportReservationRepository
        extends JpaRepository<AirportReservation, Long> {

    // ── 회원 ID로 예약 목록 조회 (단순 최신순) ──────────────
    // findByMidSorted() 로 대체됨 → 현재 미사용
    List<AirportReservation> findByMidOrderByReservedAtDesc(String mid);

    // ── 회원 ID로 예약 목록 조회 (정렬 우선순위 적용) ────────
    // MyReservationScreen 진입 시 호출
    // 정렬 순서:
    //   1. 예약완료 (depPlandTime >= 현재시각) → reservedAt 최신순
    //   2. 지난예약 (depPlandTime < 현재시각) → reservedAt 최신순
    // depPlandTime 문자열(YYYYMMDDHHMMSS) 을 현재 시각 문자열과 비교
    // now: AirportReservationServiceImpl 에서 현재 시각 문자열로 전달
    @Query("SELECT r FROM AirportReservation r " +
            "WHERE r.mid = :mid " +
            "ORDER BY " +
            "CASE WHEN r.depPlandTime >= :now THEN 0 ELSE 1 END ASC, " +
            "r.reservedAt DESC")
    List<AirportReservation> findByMidSorted(
            @Param("mid") String mid,
            @Param("now") String now
    );

    // ── 전체 예약 조회 (최신순) ───────────────────────────────
    // 관리자 기능 또는 전체 조회 시 사용 (현재 미사용)
    List<AirportReservation> findAllByOrderByReservedAtDesc();

    // ── 중복 예약 체크 ────────────────────────────────────────
    // 같은 항공편(flightNo) + 같은 탑승객 이름 + 같은 생년월일 조합으로 중복 확인
    // passengers 컬렉션 JOIN 이 필요해 JPA 메서드 방식 불가 → JPQL 직접 작성
    // AirportReservationServiceImpl.register() 에서 호출
    @Query("SELECT COUNT(r) > 0 FROM AirportReservation r " +
            "JOIN r.passengers p " +
            "WHERE r.flightNo = :flightNo " +
            "AND p.passengerName = :passengerName " +
            "AND p.passengerBirth = :passengerBirth")
    boolean existsDuplicateReservation(
            @Param("flightNo") String flightNo,
            @Param("passengerName") String passengerName,
            @Param("passengerBirth") String passengerBirth
    );
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.repository.airport.AirportReservationRepository
 * 역할  : 항공 예약 DB 접근 (JPA Repository)
 * 사용처 : AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservation.java            : 조회 대상 엔티티
 * - AirportPassenger.java              : existsDuplicateReservation JOIN 대상
 * - AirportReservationServiceImpl.java : 모든 쿼리 메서드 호출
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 구조 기준 쿼리
 *               findByPassengerName(), findByPassengerNameOrderByReservedAtDesc()
 * - 변경       : 탑승객 다중 구조 전환 후 단일 탑승객 쿼리 제거
 *               중복 예약 체크 JPA 메서드 방식 시도
 *               → passengers 컬렉션 JOIN 문제로 실패
 *               → JPQL existsDuplicateReservation() 으로 대체
 *               findByMidSorted() 추가 (예약완료 먼저, 지난예약 나중 정렬)
 * ----------------------------------------------------------------------------------
 * [메서드 목록]
 * - findByMidOrderByReservedAtDesc()   : 단순 최신순 조회 (현재 미사용)
 * - findByMidSorted(mid, now)          : 정렬 우선순위 적용 조회 (메인 사용)
 * - findAllByOrderByReservedAtDesc()   : 전체 예약 최신순 조회 (현재 미사용)
 * - existsDuplicateReservation()       : 중복 예약 체크 (JPQL)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - findByMidSorted() 의 now 파라미터:
 *   ServiceImpl 에서 LocalDateTime.now() 를 "YYYYMMDDHHMMSS" 형식으로 변환 후 전달
 * - existsDuplicateReservation() 은 passengers 컬렉션 JOIN 이 필요해
 *   JPA 메서드 방식으로 작성 불가 → JPQL 직접 작성
 * ==================================================================================
 */