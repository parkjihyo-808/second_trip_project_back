package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.entity.airport.AirportFlight;
import com.busanit401.second_trip_project_back.repository.airport.AirportFlightRepository;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
@Log4j2
public class AirportFlightRepositoryTests {

    @Autowired
    private AirportFlightRepository airportFlightRepository;

    // ── 더미 데이터 INSERT ────────────────────────────────────
    @Test
    public void testInsert() {
        log.info("✅ [AirportFlightRepositoryTests] 더미 데이터 INSERT 시작");

        List<AirportFlight> flights = List.of(
                AirportFlight.builder()
                        .airlineNm("에어부산")
                        .flightNo("BX812")
                        .depAirportId("GIMHAE")
                        .arrAirportId("GIMPO")
                        .depAirportNm("김해(부산)")
                        .arrAirportNm("김포")
                        .depPlandTime("20260501134000")
                        .arrPlandTime("20260501144500")
                        .price(53620)
                        .seatsLeft(23)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("대한항공")
                        .flightNo("KE1234")
                        .depAirportId("GIMPO")
                        .arrAirportId("JEJU")
                        .depAirportNm("김포")
                        .arrAirportNm("제주")
                        .depPlandTime("20260501090000")
                        .arrPlandTime("20260501101000")
                        .price(89000)
                        .seatsLeft(15)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("제주항공")
                        .flightNo("7C101")
                        .depAirportId("GIMPO")
                        .arrAirportId("JEJU")
                        .depAirportNm("김포")
                        .arrAirportNm("제주")
                        .depPlandTime("20260501110000")
                        .arrPlandTime("20260501121000")
                        .price(59000)
                        .seatsLeft(31)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("진에어")
                        .flightNo("LJ201")
                        .depAirportId("GIMPO")
                        .arrAirportId("JEJU")
                        .depAirportNm("김포")
                        .arrAirportNm("제주")
                        .depPlandTime("20260501140000")
                        .arrPlandTime("20260501151000")
                        .price(79000)
                        .seatsLeft(8)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("아시아나항공")
                        .flightNo("OZ8141")
                        .depAirportId("GWANGJU")
                        .arrAirportId("JEJU")
                        .depAirportNm("광주")
                        .arrAirportNm("제주")
                        .depPlandTime("20260501100000")
                        .arrPlandTime("20260501105500")
                        .price(49800)
                        .seatsLeft(42)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("에어부산")
                        .flightNo("BX802")
                        .depAirportId("GIMHAE")
                        .arrAirportId("GIMPO")
                        .depAirportNm("김해(부산)")
                        .arrAirportNm("김포")
                        .depPlandTime("20260501072000")
                        .arrPlandTime("20260501082500")
                        .price(66820)
                        .seatsLeft(31)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("대한항공")
                        .flightNo("KE1806")
                        .depAirportId("GIMHAE")
                        .arrAirportId("GIMPO")
                        .depAirportNm("김해(부산)")
                        .arrAirportNm("김포")
                        .depPlandTime("20260501071000")
                        .arrPlandTime("20260501081500")
                        .price(78700)
                        .seatsLeft(5)
                        .build(),
                AirportFlight.builder()
                        .airlineNm("제주항공")
                        .flightNo("7C305")
                        .depAirportId("GWANGJU")
                        .arrAirportId("JEJU")
                        .depAirportNm("광주")
                        .arrAirportNm("제주")
                        .depPlandTime("20260501182000")
                        .arrPlandTime("20260501192000")
                        .price(49800)
                        .seatsLeft(19)
                        .build()
        );

        airportFlightRepository.saveAll(flights);
        log.info("✅ [AirportFlightRepositoryTests] 더미 데이터 INSERT 완료 → {}건", flights.size());
    }

    // ── 전체 목록 조회 ────────────────────────────────────────
    @Test
    public void testFindAll() {
        log.info("✅ [AirportFlightRepositoryTests] 전체 목록 조회 시작");

        List<AirportFlight> list = airportFlightRepository.findAll();
        list.forEach(f -> log.info("항공편: {}", f));

        log.info("✅ [AirportFlightRepositoryTests] 전체 목록 조회 완료 → {}건", list.size());
    }

    // ── 출발/도착 + 날짜 조회 ─────────────────────────────────
    @Test
    public void testFindByDepArrAndDate() {
        log.info("✅ [AirportFlightRepositoryTests] 출발/도착/날짜 조회 시작");

        List<AirportFlight> list =
                airportFlightRepository
                        .findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
                                "GIMPO",
                                "JEJU",
                                "20260501"
                        );

        list.forEach(f -> log.info("항공편: {}", f));
        log.info("✅ [AirportFlightRepositoryTests] 조회 완료 → {}건", list.size());
    }

    // ── 단건 조회 ─────────────────────────────────────────────
    @Test
    public void testFindById() {
        log.info("✅ [AirportFlightRepositoryTests] 단건 조회 시작 → id: 1");

        airportFlightRepository.findById(1L).ifPresentOrElse(
                f -> log.info("✅ 조회 성공: {}", f),
                () -> log.error("❌ 항공편 없음 → id: 1")
        );
    }

    // ── 수정 ──────────────────────────────────────────────────
    @Test
    public void testModify() {
        log.info("✅ [AirportFlightRepositoryTests] 수정 시작 → id: 1");

        airportFlightRepository.findById(1L).ifPresentOrElse(
                f -> {
                    f.changeFlightInfo(
                            f.getAirlineNm(),
                            f.getFlightNo(),
                            f.getDepPlandTime(),
                            f.getArrPlandTime(),
                            99000,   // 가격 수정
                            10       // 잔여석 수정
                    );
                    airportFlightRepository.save(f);
                    log.info("✅ 수정 완료: {}", f);
                },
                () -> log.error("❌ 항공편 없음 → id: 1")
        );
    }

    // ── 삭제 ──────────────────────────────────────────────────
    @Test
    public void testDelete() {
        log.info("✅ [AirportFlightRepositoryTests] 삭제 시작 → id: 1");

        if (airportFlightRepository.existsById(1L)) {
            airportFlightRepository.deleteById(1L);
            log.info("✅ 삭제 완료 → id: 1");
        } else {
            log.error("❌ 항공편 없음 → id: 1");
        }
    }
}