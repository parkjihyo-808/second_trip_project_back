package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import com.busanit401.second_trip_project_back.repository.airport.AirportReservationRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class AirportReservationRepositoryTests {

    @Autowired
    private AirportReservationRepository airportReservationRepository;

    // ── 더미 데이터 INSERT ────────────────────────────────────
    @Test
    public void testInsert() {
        log.info("✅ [AirportReservationRepositoryTests] 더미 데이터 INSERT 시작");

        List<AirportReservation> reservations = List.of(
                // ── 편도 예약 ──────────────────────────────────────
                AirportReservation.builder()
                        .airlineNm("제주항공")                  // 항공사명
                        .flightNo("7C101")                      // 항공편명
                        .depAirportNm("김포")                   // 출발 공항명
                        .arrAirportNm("제주")                   // 도착 공항명
                        .depAirportId("GIMPO")                  // 출발 공항코드
                        .arrAirportId("JEJU")                   // 도착 공항코드
                        .depPlandTime("20260501080000")          // 출발 예정시각
                        .arrPlandTime("20260501091000")          // 도착 예정시각
                        .depPrice(59000)                        // 가는편 가격
                        .retAirlineNm(null)                     // 오는편 항공사명
                        .retFlightNo(null)                      // 오는편 항공편명
                        .retDepPlandTime(null)                  // 오는편 출발 예정시각
                        .retArrPlandTime(null)                  // 오는편 도착 예정시각
                        .retPrice(null)                         // 오는편 가격
//                        .passengerName("홍길동")               // 탑승객 이름
//                        .passengerBirth("19990101")             // 생년월일
//                        .passengerGender("남성")                // 성별
                        .isRoundTrip(false)                     // 편도/왕복
                        .reservedAt("2026-04-15 10:00:00")      // 예약 일시
                        .build(),

                // ── 왕복 예약 ──────────────────────────────────────
                AirportReservation.builder()
                        .airlineNm("대한항공")                  // 항공사명
                        .flightNo("KE1201")                     // 항공편명
                        .depAirportNm("김포")                   // 출발 공항명
                        .arrAirportNm("제주")                   // 도착 공항명
                        .depAirportId("GIMPO")                  // 출발 공항코드
                        .arrAirportId("JEJU")                   // 도착 공항코드
                        .depPlandTime("20260501060000")          // 출발 예정시각
                        .arrPlandTime("20260501071000")          // 도착 예정시각
                        .depPrice(89000)                        // 가는편 가격
                        .retAirlineNm("대한항공")               // 오는편 항공사명
                        .retFlightNo("KE1202")                  // 오는편 항공편명
                        .retDepPlandTime("20260503090000")       // 오는편 출발 예정시각
                        .retArrPlandTime("20260503101000")       // 오는편 도착 예정시각
                        .retPrice(95000)                        // 오는편 가격
//                        .passengerName("김철수")               // 탑승객 이름
//                        .passengerBirth("19850315")             // 생년월일
//                        .passengerGender("남성")                // 성별
                        .isRoundTrip(true)                      // 편도/왕복
                        .reservedAt("2026-04-15 11:00:00")      // 예약 일시
                        .build()
        );

        airportReservationRepository.saveAll(reservations);
        log.info("✅ [AirportReservationRepositoryTests] INSERT 완료 → {}건",
                reservations.size());
    }

    // ── 전체 목록 조회 ────────────────────────────────────────
    @Test
    public void testFindAll() {
        log.info("✅ [AirportReservationRepositoryTests] 전체 목록 조회 시작");

        List<AirportReservation> list =
                airportReservationRepository.findAllByOrderByReservedAtDesc();
        list.forEach(r -> log.info("예약: {}", r));

        log.info("✅ [AirportReservationRepositoryTests] 조회 완료 → {}건",
                list.size());
    }

    // ── 탑승객 이름으로 조회 ──────────────────────────────────
//    @Test
//    public void testFindByName() {
//        log.info("✅ [AirportReservationRepositoryTests] 탑승객 이름으로 조회 시작");
//
//        List<AirportReservation> list =
//                airportReservationRepository
//                        .findByPassengerNameOrderByReservedAtDesc("홍길동");
//        list.forEach(r -> log.info("예약: {}", r));
//
//        log.info("✅ [AirportReservationRepositoryTests] 조회 완료 → {}건",
//                list.size());
//    }

    // ── 단건 조회 ─────────────────────────────────────────────
    @Test
    public void testFindById() {
        log.info("✅ [AirportReservationRepositoryTests] 단건 조회 시작 → id: 1");

        airportReservationRepository.findById(1L).ifPresentOrElse(
                r -> log.info("✅ 조회 성공: {}", r),
                () -> log.error("❌ 예약 없음 → id: 1")
        );
    }

    // ── 삭제 ──────────────────────────────────────────────────
    @Test
    public void testDelete() {
        log.info("✅ [AirportReservationRepositoryTests] 삭제 시작 → id: 1");

        if (airportReservationRepository.existsById(1L)) {
            airportReservationRepository.deleteById(1L);
            log.info("✅ 삭제 완료 → id: 1");
        } else {
            log.error("❌ 예약 없음 → id: 1");
        }
    }
}