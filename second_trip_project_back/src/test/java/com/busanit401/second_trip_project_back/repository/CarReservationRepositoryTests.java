package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;
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
public class CarReservationRepositoryTests {

    @Autowired
    private CarReservationRepository carReservationRepository;

    // ─────────────────────────────────────────────
    // 기본 JPA 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("예약 전체 조회")
    void findAll_returnsReservations() {
        List<CarReservation> all = carReservationRepository.findAll();
        assertThat(all).isNotNull();
    }

    @Test
    @DisplayName("예약 ID로 단건 조회")
    void findById_returnsReservation() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        Long id = all.get(0).getId();
        CarReservation found = carReservationRepository.findById(id).orElse(null);

        assertThat(found).isNotNull();
        assertThat(found.getId()).isEqualTo(id);
    }

    // ─────────────────────────────────────────────
    // searchExistsOverlap 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("겹치는 예약이 있는 차량 - true 반환")
    void searchExistsOverlap_returnsTrue_whenOverlapExists() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        CarReservation sample = all.get(0);
        Long carId = sample.getCar().getId();
        LocalDateTime start = sample.getStartDate();
        LocalDateTime end = sample.getEndDate();

        // 같은 기간으로 조회 → 반드시 true
        boolean result = carReservationRepository.searchExistsOverlap(carId, start, end);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("겹치는 예약이 없는 날짜 - false 반환")
    void searchExistsOverlap_returnsFalse_whenNoOverlap() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        Long carId = all.get(0).getCar().getId();

        // 2000년대 초 날짜는 예약이 없을 것으로 가정
        boolean result = carReservationRepository.searchExistsOverlap(
                carId,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2000, 1, 2, 0, 0));

        assertThat(result).isFalse();
    }

    // ─────────────────────────────────────────────
    // searchExistsUserOverlap 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("특정 사용자의 겹치는 예약 존재 - true 반환")
    void searchExistsUserOverlap_returnsTrue_whenOverlapExists() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        CarReservation sample = all.get(0);
        String mid = sample.getUser().getMid();
        LocalDateTime start = sample.getStartDate();
        LocalDateTime end = sample.getEndDate();

        boolean result = carReservationRepository.searchExistsUserOverlap(mid, start, end);
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("특정 사용자의 겹치는 예약 없음 - false 반환")
    void searchExistsUserOverlap_returnsFalse_whenNoOverlap() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        String mid = all.get(0).getUser().getMid();

        boolean result = carReservationRepository.searchExistsUserOverlap(
                mid,
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2000, 1, 2, 0, 0));

        assertThat(result).isFalse();
    }

    // ─────────────────────────────────────────────
    // searchUnavailableCarIds 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("넓은 날짜 범위로 예약 불가 차량 ID 조회 - 중복 없음")
    void searchUnavailableCarIds_returnsDistinctIds() {
        List<Long> ids = carReservationRepository.searchUnavailableCarIds(
                LocalDateTime.of(2020, 1, 1, 0, 0),
                LocalDateTime.of(2030, 12, 31, 0, 0));

        assertThat(ids).isNotNull();
        // 중복 없음 확인 (distinct)
        assertThat(ids).doesNotHaveDuplicates();
    }

    @Test
    @DisplayName("예약이 없는 날짜 범위 - 빈 리스트 반환")
    void searchUnavailableCarIds_returnsEmpty_whenNoReservations() {
        List<Long> ids = carReservationRepository.searchUnavailableCarIds(
                LocalDateTime.of(2000, 1, 1, 0, 0),
                LocalDateTime.of(2000, 1, 2, 0, 0));

        assertThat(ids).isEmpty();
    }

    @Test
    @DisplayName("예약이 있는 차량 ID는 불가 목록에 포함됨")
    void searchUnavailableCarIds_containsReservedCarId() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        CarReservation sample = all.get(0);
        Long carId = sample.getCar().getId();
        LocalDateTime start = sample.getStartDate();
        LocalDateTime end = sample.getEndDate();

        List<Long> ids = carReservationRepository.searchUnavailableCarIds(start, end);
        assertThat(ids).contains(carId);
    }

    // ─────────────────────────────────────────────
    // searchByUserMidWithCursor 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("사용자 mid로 커서 기반 예약 조회 - 첫 페이지")
    void searchByUserMidWithCursor_returnsReservations() {
        List<CarReservation> all = carReservationRepository.findAll();
        if (all.isEmpty()) return;

        String mid = all.get(0).getUser().getMid();

        List<CarReservation> result = carReservationRepository.searchByUserMidWithCursor(
                mid,
                0,
                LocalDateTime.of(9999, 12, 31, 0, 0),
                Long.MAX_VALUE,
                10);

        assertThat(result).isNotNull();
    }

    @Test
    @DisplayName("존재하지 않는 사용자 mid로 조회 - 빈 리스트 반환")
    void searchByUserMidWithCursor_returnsEmpty_forUnknownMid() {
        List<CarReservation> result = carReservationRepository.searchByUserMidWithCursor(
                "없는유저_xyz",
                0,
                LocalDateTime.of(9999, 12, 31, 0, 0),
                Long.MAX_VALUE,
                10);

        assertThat(result).isEmpty();
    }
}