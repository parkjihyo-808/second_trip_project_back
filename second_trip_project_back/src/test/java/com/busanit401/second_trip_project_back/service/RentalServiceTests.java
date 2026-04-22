package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;
import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.dto.car.CarReservationCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationRequestDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.car.CarRepository;
import com.busanit401.second_trip_project_back.repository.car.CarReservationRepository;
import com.busanit401.second_trip_project_back.service.car.RentalService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
public class RentalServiceTests {

    @Autowired
    private RentalService rentalService;

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CarReservationRepository carReservationRepository;

    // ─────────────────────────────────────────────
    // createReservation 예외 케이스
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("반납 시간이 대여 시간보다 이전이면 예외")
    void createReservation_throwsException_whenEndBeforeStart() {
        CarReservationRequestDTO request = new CarReservationRequestDTO();
        request.setCarId(1L);
        request.setStartDate(LocalDateTime.of(2025, 8, 3, 10, 0));
        request.setEndDate(LocalDateTime.of(2025, 8, 1, 10, 0)); // 시작보다 이전

        assertThatThrownBy(() -> rentalService.createReservation("anyUser", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("반납 시간은 대여 시간 이후여야 합니다.");
    }

    @Test
    @DisplayName("존재하지 않는 차량 ID면 예외")
    void createReservation_throwsException_whenCarNotFound() {
        CarReservationRequestDTO request = new CarReservationRequestDTO();
        request.setCarId(Long.MAX_VALUE); // 존재하지 않는 ID
        request.setStartDate(LocalDateTime.of(2025, 8, 1, 10, 0));
        request.setEndDate(LocalDateTime.of(2025, 8, 3, 10, 0));

        assertThatThrownBy(() -> rentalService.createReservation("anyUser", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("차량을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("존재하지 않는 사용자면 예외")
    void createReservation_throwsException_whenUserNotFound() {
        // DB에 실제 차량이 있을 경우에만 실행
        if (carRepository.findAll().isEmpty()) return;

        Long carId = carRepository.findAll().get(0).getId();

        CarReservationRequestDTO request = new CarReservationRequestDTO();
        request.setCarId(carId);
        request.setStartDate(LocalDateTime.of(2025, 8, 1, 10, 0));
        request.setEndDate(LocalDateTime.of(2025, 8, 3, 10, 0));

        assertThatThrownBy(() -> rentalService.createReservation("존재하지않는유저_xyz", request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    // ─────────────────────────────────────────────
    // cancelReservation 예외 케이스
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("존재하지 않는 예약 취소 시 예외")
    void cancelReservation_throwsException_whenReservationNotFound() {
        assertThatThrownBy(() -> rentalService.cancelReservation("anyUser", Long.MAX_VALUE))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("예약을 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("본인 예약이 아닌 경우 취소 시 예외")
    void cancelReservation_throwsException_whenNotOwner() {
        List<CarReservation> reservations = carReservationRepository.findAll();
        if (reservations.isEmpty()) return;

        CarReservation reservation = reservations.get(0);
        Long reservationId = reservation.getId();
        String ownerMid = reservation.getUser().getMid();

        // 다른 사용자로 취소 시도
        assertThatThrownBy(() -> rentalService.cancelReservation("다른유저_xyz", reservationId))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("본인 예약만 취소할 수 있습니다.");
    }

    // ─────────────────────────────────────────────
    // getMyReservationsCursor 테스트
    // ─────────────────────────────────────────────

    @Test
    @DisplayName("예약이 있는 사용자 커서 조회 - 결과 반환")
    void getMyReservationsCursor_returnsResult() {
        List<CarReservation> reservations = carReservationRepository.findAll();
        if (reservations.isEmpty()) return;

        String mid = reservations.get(0).getUser().getMid();

        CarReservationCursorResponseDTO result = rentalService.getMyReservationsCursor(
                mid, 0, LocalDateTime.of(9999, 12, 31, 0, 0), Long.MAX_VALUE, 10);

        assertThat(result).isNotNull();
        assertThat(result.getReservation()).isNotEmpty();
    }

    @Test
    @DisplayName("예약이 없는 사용자 커서 조회 - 빈 결과")
    void getMyReservationsCursor_returnsEmpty_forUnknownUser() {
        CarReservationCursorResponseDTO result = rentalService.getMyReservationsCursor(
                "없는유저_xyz", 0, LocalDateTime.of(9999, 12, 31, 0, 0), Long.MAX_VALUE, 10);

        assertThat(result.isHasNext()).isFalse();
        assertThat(result.getReservation()).isEmpty();
    }

    @Test
    @DisplayName("커서 조회 결과 hasNext - size보다 많으면 true")
    void getMyReservationsCursor_hasNext_whenMoreThanPageSize() {
        // 각 멤버의 예약 수를 cursor 쿼리로 직접 확인
        List<Member> members = memberRepository.findAll();

        for (Member member : members) {
            String mid = member.getMid();
            List<CarReservation> userReservations = carReservationRepository
                    .searchByUserMidWithCursor(mid, 0, LocalDateTime.of(9999, 12, 31, 0, 0), Long.MAX_VALUE, 3);

            if (userReservations.size() >= 2) {
                // 예약이 2개 이상인 유저에 대해 size=1로 조회 → hasNext=true
                CarReservationCursorResponseDTO result = rentalService.getMyReservationsCursor(
                        mid, 0, LocalDateTime.of(9999, 12, 31, 0, 0), Long.MAX_VALUE, 1);

                assertThat(result.isHasNext()).isTrue();
                assertThat(result.getReservation()).hasSize(1);
                assertThat(result.getNextCursorId()).isNotNull();
                return;
            }
        }
        // 예약 2개 이상인 유저가 없으면 스킵
    }

    @Test
    @DisplayName("실제 회원 목록 조회")
    void memberRepository_findAll_returnsMembers() {
        List<Member> members = memberRepository.findAll();
        assertThat(members).isNotNull();
    }
}