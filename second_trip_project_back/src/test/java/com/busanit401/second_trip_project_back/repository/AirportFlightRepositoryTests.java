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
    // 발표 시연용: 4월 22일 ~ 4월 30일 데이터 포함
    // 노선: 김해↔제주, 김해↔김포, 김포↔제주, 광주↔제주
    @Test
    public void testInsert() {
        log.info("✅ [AirportFlightRepositoryTests] 더미 데이터 INSERT 시작");

        List<AirportFlight> flights = List.of(

                // ── 김해(부산) → 제주 ─────────────────────────
                AirportFlight.builder()
                        .airlineNm("대한항공").flightNo("KE1001")
                        .depAirportId("GIMHAE").arrAirportId("JEJU")
                        .depAirportNm("김해(부산)").arrAirportNm("제주")
                        .depPlandTime("20260422060000").arrPlandTime("20260422071000")
                        .price(89000).seatsLeft(20).build(),

                AirportFlight.builder()
                        .airlineNm("아시아나항공").flightNo("OZ8901")
                        .depAirportId("GIMHAE").arrAirportId("JEJU")
                        .depAirportNm("김해(부산)").arrAirportNm("제주")
                        .depPlandTime("20260422090000").arrPlandTime("20260422101000")
                        .price(79000).seatsLeft(15).build(),

                AirportFlight.builder()
                        .airlineNm("에어부산").flightNo("BX701")
                        .depAirportId("GIMHAE").arrAirportId("JEJU")
                        .depAirportNm("김해(부산)").arrAirportNm("제주")
                        .depPlandTime("20260422130000").arrPlandTime("20260422141000")
                        .price(59000).seatsLeft(8).build(),

                AirportFlight.builder()
                        .airlineNm("제주항공").flightNo("7C501")
                        .depAirportId("GIMHAE").arrAirportId("JEJU")
                        .depAirportNm("김해(부산)").arrAirportNm("제주")
                        .depPlandTime("20260422180000").arrPlandTime("20260422191000")
                        .price(69000).seatsLeft(31).build(),

                AirportFlight.builder()
                        .airlineNm("진에어").flightNo("LJ601")
                        .depAirportId("GIMHAE").arrAirportId("JEJU")
                        .depAirportNm("김해(부산)").arrAirportNm("제주")
                        .depPlandTime("20260422200000").arrPlandTime("20260422211000")
                        .price(55000).seatsLeft(0).build(),  // 마감 테스트용

                // ── 제주 → 김해(부산) ─────────────────────────
                AirportFlight.builder()
                        .airlineNm("대한항공").flightNo("KE1062")
                        .depAirportId("JEJU").arrAirportId("GIMHAE")
                        .depAirportNm("제주").arrAirportNm("김해(부산)")
                        .depPlandTime("20260423060000").arrPlandTime("20260423071000")
                        .price(89000).seatsLeft(18).build(),

                AirportFlight.builder()
                        .airlineNm("에어부산").flightNo("BX702")
                        .depAirportId("JEJU").arrAirportId("GIMHAE")
                        .depAirportNm("제주").arrAirportNm("김해(부산)")
                        .depPlandTime("20260423140000").arrPlandTime("20260423151000")
                        .price(59000).seatsLeft(22).build(),

                AirportFlight.builder()
                        .airlineNm("제주항공").flightNo("7C502")
                        .depAirportId("JEJU").arrAirportId("GIMHAE")
                        .depAirportNm("제주").arrAirportNm("김해(부산)")
                        .depPlandTime("20260423190000").arrPlandTime("20260423201000")
                        .price(65000).seatsLeft(9).build(),

                // ── 김포 → 제주 ───────────────────────────────
                AirportFlight.builder()
                        .airlineNm("대한항공").flightNo("KE1234")
                        .depAirportId("GIMPO").arrAirportId("JEJU")
                        .depAirportNm("김포").arrAirportNm("제주")
                        .depPlandTime("20260422090000").arrPlandTime("20260422101000")
                        .price(89000).seatsLeft(15).build(),

                AirportFlight.builder()
                        .airlineNm("제주항공").flightNo("7C101")
                        .depAirportId("GIMPO").arrAirportId("JEJU")
                        .depAirportNm("김포").arrAirportNm("제주")
                        .depPlandTime("20260422110000").arrPlandTime("20260422121000")
                        .price(59000).seatsLeft(31).build(),

                AirportFlight.builder()
                        .airlineNm("진에어").flightNo("LJ201")
                        .depAirportId("GIMPO").arrAirportId("JEJU")
                        .depAirportNm("김포").arrAirportNm("제주")
                        .depPlandTime("20260422140000").arrPlandTime("20260422151000")
                        .price(79000).seatsLeft(8).build(),

                // ── 김해(부산) → 김포 ─────────────────────────
                AirportFlight.builder()
                        .airlineNm("에어부산").flightNo("BX812")
                        .depAirportId("GIMHAE").arrAirportId("GIMPO")
                        .depAirportNm("김해(부산)").arrAirportNm("김포")
                        .depPlandTime("20260422134000").arrPlandTime("20260422144500")
                        .price(53620).seatsLeft(23).build(),

                AirportFlight.builder()
                        .airlineNm("대한항공").flightNo("KE1806")
                        .depAirportId("GIMHAE").arrAirportId("GIMPO")
                        .depAirportNm("김해(부산)").arrAirportNm("김포")
                        .depPlandTime("20260422071000").arrPlandTime("20260422081500")
                        .price(78700).seatsLeft(5).build(),

                // ── 광주 → 제주 ───────────────────────────────
                AirportFlight.builder()
                        .airlineNm("아시아나항공").flightNo("OZ8141")
                        .depAirportId("GWANGJU").arrAirportId("JEJU")
                        .depAirportNm("광주").arrAirportNm("제주")
                        .depPlandTime("20260422100000").arrPlandTime("20260422105500")
                        .price(49800).seatsLeft(42).build(),

                AirportFlight.builder()
                        .airlineNm("제주항공").flightNo("7C305")
                        .depAirportId("GWANGJU").arrAirportId("JEJU")
                        .depAirportNm("광주").arrAirportNm("제주")
                        .depPlandTime("20260422182000").arrPlandTime("20260422192000")
                        .price(49800).seatsLeft(19).build()
        );

        airportFlightRepository.saveAll(flights);
        log.info("✅ [AirportFlightRepositoryTests] INSERT 완료 → {}건", flights.size());
    }

    // ── 전체 목록 조회 ────────────────────────────────────────
    @Test
    public void testFindAll() {
        log.info("✅ [AirportFlightRepositoryTests] 전체 목록 조회 시작");
        List<AirportFlight> list = airportFlightRepository.findAll();
        list.forEach(f -> log.info("항공편: {} {} {} → {}",
                f.getAirlineNm(), f.getFlightNo(), f.getDepAirportId(), f.getArrAirportId()));
        log.info("✅ [AirportFlightRepositoryTests] 조회 완료 → {}건", list.size());
    }

    // ── 출발/도착 + 날짜 조회 테스트 ─────────────────────────
    // 시연 메인 노선: 김해 → 제주 / 4월 22일
    @Test
    public void testFindByDepArrAndDate() {
        log.info("✅ [AirportFlightRepositoryTests] 출발/도착/날짜 조회 시작");
        List<AirportFlight> list =
                airportFlightRepository
                        .findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
                                "GIMHAE", "JEJU", "20260422"
                        );
        list.forEach(f -> log.info("항공편: {} {} 잔여석: {}석 가격: {}원",
                f.getAirlineNm(), f.getFlightNo(), f.getSeatsLeft(), f.getPrice()));
        log.info("✅ [AirportFlightRepositoryTests] 조회 완료 → {}건", list.size());
    }

    // ── 단건 조회 ─────────────────────────────────────────────
    @Test
    public void testFindById() {
        log.info("✅ [AirportFlightRepositoryTests] 단건 조회 → id: 1");
        airportFlightRepository.findById(1L).ifPresentOrElse(
                f -> log.info("✅ 조회 성공: {} {}", f.getAirlineNm(), f.getFlightNo()),
                () -> log.error("❌ 항공편 없음 → id: 1")
        );
    }

    // ── 수정 ──────────────────────────────────────────────────
    @Test
    public void testModify() {
        log.info("✅ [AirportFlightRepositoryTests] 수정 → id: 1");
        airportFlightRepository.findById(1L).ifPresentOrElse(
                f -> {
                    f.changeFlightInfo(
                            f.getAirlineNm(), f.getFlightNo(),
                            f.getDepPlandTime(), f.getArrPlandTime(),
                            99000, 10
                    );
                    airportFlightRepository.save(f);
                    log.info("✅ 수정 완료 → 가격: 99000 / 잔여석: 10");
                },
                () -> log.error("❌ 항공편 없음 → id: 1")
        );
    }

    // ── 삭제 ──────────────────────────────────────────────────
    @Test
    public void testDelete() {
        log.info("✅ [AirportFlightRepositoryTests] 삭제 → id: 1");
        if (airportFlightRepository.existsById(1L)) {
            airportFlightRepository.deleteById(1L);
            log.info("✅ 삭제 완료 → id: 1");
        } else {
            log.error("❌ 항공편 없음 → id: 1");
        }
    }
}