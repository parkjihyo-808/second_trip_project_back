package com.busanit401.second_trip_project_back.repository.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AirportReservationRepository
        extends JpaRepository<AirportReservation, Long> {

    // ── 회원 ID로 예약 조회 ───────────────────────────────────
//    List<AirportReservation> findByMemberIdOrderByReservedAtDesc(String mid);
    List<AirportReservation> findByMidOrderByReservedAtDesc(String mid);

    // ── 탑승객 이름으로 예약 조회 ─────────────────────────────
//    List<AirportReservation> findByPassengerName(String passengerName);

    // ── 예약 일시 내림차순 전체 조회 ─────────────────────────
    List<AirportReservation> findAllByOrderByReservedAtDesc();


    // ── 탑승객 이름 + 내림차순 조회 ──────────────────────────
//    List<AirportReservation> findByPassengerNameOrderByReservedAtDesc(
//            String passengerName);
}