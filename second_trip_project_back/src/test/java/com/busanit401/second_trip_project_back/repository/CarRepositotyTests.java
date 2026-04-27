package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import com.busanit401.second_trip_project_back.repository.car.CarRepository;
import com.busanit401.second_trip_project_back.repository.car.CarReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CarRepositotyTests {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarReservationRepository carReservationRepository;

    // ─────────────────────────────────────────────
    // CarRepository (CarSearch) 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("차량 전체 조회")
    void findAll_returnsCars() {
        List<Car> all = carRepository.findAll();
        assertThat(all).isNotEmpty();
    }

    @Test
    @DisplayName("지역으로 차종 커서 기반 조회 - 첫 페이지")
    void searchCarTypesByRegionWithCursor_firstPage() {
        List<Car> all = carRepository.findAll();
        if (all.isEmpty()) return;

        String region = all.get(0).getCompany().getRegion();

        List<CarTypeDTO> result = carRepository.searchCarTypesByRegionWithCursor(
                region, List.of(), 0, "", 10);

        assertThat(result).isNotEmpty();
        assertThat(result.get(0).getLowestPrice()).isGreaterThan(0);
    }

    @Test
    @DisplayName("차종 이름 리스트로 차량 옵션 조회")
    void searchOptionsByCarNamesIn_returnsOptions() {
        List<Car> all = carRepository.findAll();
        if (all.isEmpty()) return;

        String carName = all.get(0).getName();
        String region = all.get(0).getCompany().getRegion();

        List<Car> result = carRepository.searchOptionsByCarNamesIn(
                List.of(carName), region, List.of());

        assertThat(result).isNotEmpty();
    }

    @Test
    @DisplayName("차량 ID로 단건 조회")
    void findById_returnsCar() {
        List<Car> all = carRepository.findAll();
        if (all.isEmpty()) return;

        Long id = all.get(0).getId();
        Car found = carRepository.findById(id).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(id);
    }

    // ─────────────────────────────────────────────
    // CarReservationRepository (CarReservationSearch) 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("예약 불가 차량 ID 목록 조회 - 결과 반환")
    void searchUnavailableCarIds_returnsIds() {
        // 넓은 날짜 범위로 예약된 차량이 있으면 반환, 없으면 빈 리스트
        List<Long> unavailable = carReservationRepository.searchUnavailableCarIds(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2030, 12, 31, 0, 0));

        assertThat(unavailable).isNotNull();
    }

    @Test
    @DisplayName("겹치지 않는 날짜로 중복 예약 확인 - false")
    void searchExistsOverlap_returnsFalse_forPastDate() {
        List<Long> allCarIds = carReservationRepository.searchUnavailableCarIds(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2020, 1, 2, 0, 0));

        // 2020년 1월 1일 데이터가 없다면 false
        if (allCarIds.isEmpty()) {
            boolean result = carReservationRepository.searchExistsOverlap(
                    1L,
                    LocalDateTime.of(2020, 1, 1, 0, 0),
                    LocalDateTime.of(2020, 1, 2, 0, 0));
            assertThat(result).isFalse();
        }
    }
}