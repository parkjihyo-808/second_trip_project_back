package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.member.MemberRole;
import com.busanit401.second_trip_project_back.dto.loging.ReservationRequestDTO;
import com.busanit401.second_trip_project_back.dto.loging.ReservationResponseDTO;
import com.busanit401.second_trip_project_back.entity.Reservation;
import com.busanit401.second_trip_project_back.enums.ReservationStatus;
import com.busanit401.second_trip_project_back.repository.ReservationRepository;
import com.busanit401.second_trip_project_back.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class) // Mock 객체 사용
@DisplayName("예약 서비스 테스트")
class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationService reservationService;

    private Member testMember;
    private ReservationRequestDTO requestDTO;

    @BeforeEach // 각 테스트 실행 전에 공통 데이터 세팅
    void setUp() {
        // 테스트용 회원 생성
        testMember = Member.builder()
                .id(1L)
                .mid("test@test.com")
                .mpw("1234")
                .mname("테스터")
                .email("test@test.com")
                .role(MemberRole.USER)
                .build();

        // 테스트용 예약 요청 DTO 생성
        requestDTO = ReservationRequestDTO.builder()
                .contentId("126508")
                .roomCode("ROOM001")
                .accommodationTitle("해운대 그랜드 호텔")
                .roomTitle("스탠다드룸")
                .checkInDate(LocalDate.of(2024, 5, 1))
                .checkOutDate(LocalDate.of(2024, 5, 3))
                .guestCount(2)
                .totalPrice(200000)
                .build();
    }

    @Test
    @DisplayName("예약 생성 성공")
    void createReservation_success() {
        // given (준비)
        // 중복 예약 없음
        given(reservationRepository.findOverlappingReservations(
                any(), any(), any(), any()))
                .willReturn(Collections.emptyList());

        // 저장 성공
        Reservation savedReservation = Reservation.builder()
                .id(1L)
                .member(testMember)
                .contentId(requestDTO.getContentId())
                .roomCode(requestDTO.getRoomCode())
                .accommodationTitle(requestDTO.getAccommodationTitle())
                .roomTitle(requestDTO.getRoomTitle())
                .checkInDate(requestDTO.getCheckInDate())
                .checkOutDate(requestDTO.getCheckOutDate())
                .guestCount(requestDTO.getGuestCount())
                .totalPrice(requestDTO.getTotalPrice())
                .status(ReservationStatus.PENDING)
                .build();

        given(reservationRepository.save(any()))
                .willReturn(savedReservation);

        // when (실행)
        ReservationResponseDTO response =
                reservationService.createReservation(testMember, requestDTO);

        // then (검증)
        assertThat(response).isNotNull();
        assertThat(response.getContentId()).isEqualTo("126508");
        assertThat(response.getRoomTitle()).isEqualTo("스탠다드룸");
        assertThat(response.getStatus()).isEqualTo(ReservationStatus.PENDING);
        verify(reservationRepository).save(any());
    }

    @Test
    @DisplayName("날짜 중복 예약 실패")
    void createReservation_fail_duplicateDate() {
        // given
        // 이미 예약이 있는 상황
        Reservation existingReservation = Reservation.builder()
                .id(1L)
                .member(testMember)
                .contentId("126508")
                .roomCode("ROOM001")
                .checkInDate(LocalDate.of(2024, 5, 1))
                .checkOutDate(LocalDate.of(2024, 5, 3))
                .status(ReservationStatus.CONFIRMED)
                .build();

        given(reservationRepository.findOverlappingReservations(
                any(), any(), any(), any()))
                .willReturn(List.of(existingReservation));

        // when & then
        // 예외가 발생해야 함
        assertThatThrownBy(() ->
                reservationService.createReservation(testMember, requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("해당 날짜에 이미 예약이 있습니다.");
    }

    @Test
    @DisplayName("예약 취소 성공")
    void cancelReservation_success() {
        // given
        Reservation reservation = Reservation.builder()
                .id(1L)
                .member(testMember)
                .contentId("126508")
                .roomCode("ROOM001")
                .checkInDate(LocalDate.of(2024, 5, 1))
                .checkOutDate(LocalDate.of(2024, 5, 3))
                .status(ReservationStatus.CONFIRMED)
                .build();

        given(reservationRepository.findById(1L))
                .willReturn(Optional.of(reservation));

        // when
        ReservationResponseDTO response =
                reservationService.cancelReservation(testMember, 1L);

        // then
        assertThat(response.getStatus())
                .isEqualTo(ReservationStatus.CANCELLED);
    }

    @Test
    @DisplayName("다른 사람 예약 취소 실패")
    void cancelReservation_fail_notOwner() {
        // given
        // 다른 회원의 예약
        Member otherMember = Member.builder()
                .id(2L)
                .mid("other@test.com")
                .mpw("1234")
                .mname("다른사람")
                .email("other@test.com")
                .role(MemberRole.USER)
                .build();

        Reservation reservation = Reservation.builder()
                .id(1L)
                .member(otherMember) // 다른 회원의 예약
                .contentId("126508")
                .roomCode("ROOM001")
                .status(ReservationStatus.CONFIRMED)
                .build();

        given(reservationRepository.findById(1L))
                .willReturn(Optional.of(reservation));

        // when & then
        assertThatThrownBy(() ->
                reservationService.cancelReservation(testMember, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("본인의 예약만 취소할 수 있습니다.");
    }
}
