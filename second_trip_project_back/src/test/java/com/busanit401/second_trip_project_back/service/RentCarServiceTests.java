package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchResultDTO;
import com.busanit401.second_trip_project_back.service.car.RentCarService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RentCarServiceTests {

    @Autowired
    private RentCarService rentCarService;

    // ─────────────────────────────────────────────
    // getRegions 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("지역 목록 조회 - null 아님")
    void getRegions_returnsNotNull() {
        List<String> regions = rentCarService.getRegions();
        assertThat(regions).isNotNull();
    }

    @Test
    @DisplayName("지역 목록 조회 - 중복 없음")
    void getRegions_returnsDistinct() {
        List<String> regions = rentCarService.getRegions();
        assertThat(regions).doesNotHaveDuplicates();
    }

    // ─────────────────────────────────────────────
    // searchCarsWithCompanyCars 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("차종+업체 커서 조회 - 첫 페이지 결과 구조 확인")
    void searchCarsWithCompanyCars_returnsValidStructure() {
        List<String> regions = rentCarService.getRegions();
        if (regions.isEmpty()) return;

        String region = regions.get(0);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 2);

        CarSearchCursorResponseDTO result = rentCarService.searchCarsWithCompanyCars(
                region, start, end, 0, "", 10);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).isNotNull();
    }

    @Test
    @DisplayName("차종+업체 커서 조회 - 결과가 있으면 각 항목에 차이름, 최저가 존재")
    void searchCarsWithCompanyCars_eachItemHasRequiredFields() {
        List<String> regions = rentCarService.getRegions();
        if (regions.isEmpty()) return;

        String region = regions.get(0);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 2);

        CarSearchCursorResponseDTO result = rentCarService.searchCarsWithCompanyCars(
                region, start, end, 0, "", 10);

        for (CarSearchResultDTO item : result.getContent()) {
            assertThat(item.getCarName()).isNotBlank();
            assertThat(item.getLowestPrice()).isGreaterThan(0);
            assertThat(item.getCompanyCarDTOs()).isNotNull();
        }
    }

    @Test
    @DisplayName("존재하지 않는 지역으로 조회 - 빈 결과")
    void searchCarsWithCompanyCars_returnsEmpty_whenRegionNotFound() {
        CarSearchCursorResponseDTO result = rentCarService.searchCarsWithCompanyCars(
                "없는지역_xyz",
                LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 3),
                0, "", 10);

        assertThat(result.getContent()).isEmpty();
        assertThat(result.isHasNext()).isFalse();
    }

    @Test
    @DisplayName("커서 페이지네이션 - size만큼 반환되면 hasNext=true")
    void searchCarsWithCompanyCars_hasNext_whenResultMatchesSize() {
        List<String> regions = rentCarService.getRegions();
        if (regions.isEmpty()) return;

        String region = regions.get(0);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 2);

        CarSearchCursorResponseDTO all = rentCarService.searchCarsWithCompanyCars(
                region, start, end, 0, "", 100);
        int totalTypes = all.getContent().size();
        if (totalTypes < 2) return;

        CarSearchCursorResponseDTO result = rentCarService.searchCarsWithCompanyCars(
                region, start, end, 0, "", 1);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.isHasNext()).isTrue();
        assertThat(result.getNextCursorPrice()).isNotNull();
        assertThat(result.getNextCursorName()).isNotBlank();
    }

    @Test
    @DisplayName("커서로 두 번째 페이지 조회 - 첫 페이지와 다른 결과")
    void searchCarsWithCompanyCars_secondPage_returnsDifferentResult() {
        List<String> regions = rentCarService.getRegions();
        if (regions.isEmpty()) return;

        String region = regions.get(0);
        LocalDate start = LocalDate.of(2020, 1, 1);
        LocalDate end = LocalDate.of(2020, 1, 2);

        CarSearchCursorResponseDTO first = rentCarService.searchCarsWithCompanyCars(
                region, start, end, 0, "", 1);
        if (!first.isHasNext()) return;

        CarSearchCursorResponseDTO second = rentCarService.searchCarsWithCompanyCars(
                region, start, end,
                first.getNextCursorPrice(), first.getNextCursorName(), 1);

        assertThat(second.getContent()).isNotEmpty();
        assertThat(second.getContent().get(0).getCarName())
                .isNotEqualTo(first.getContent().get(0).getCarName());
    }
}