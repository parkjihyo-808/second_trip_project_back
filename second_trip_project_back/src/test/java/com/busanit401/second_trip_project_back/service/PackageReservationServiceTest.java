package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.PackageItem;
import com.busanit401.second_trip_project_back.domain.packages.PackageReservation;
import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.PackageItemRepository;
import com.busanit401.second_trip_project_back.repository.PackageReservationRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Mockito 사용 선언
class PackageReservationServiceTest {

    @Mock
    private PackageReservationRepository reservationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private PackageItemRepository packageItemRepository;

    @InjectMocks
    private PackageReservationServiceImpl reservationService;

    @Test
    @DisplayName("예약 등록 성공 테스트")
    void registerSuccess() {
        // 1. Given (준비: 가짜 데이터 설정)
        PackageReservationDTO dto = PackageReservationDTO.builder()
                .packageId(1L)
                .reservationDate(LocalDate.now())
                .peopleCount(2)
                .totalPrice(200000)
                .build();

        Member mockMember = Member.builder().id(1L).build();
        PackageItem mockPackage = PackageItem.builder().id(1L).title("제주도 패키지").build();
        PackageReservation mockReservation = PackageReservation.builder().id(100L).build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(mockMember));
        when(packageItemRepository.findById(1L)).thenReturn(Optional.of(mockPackage));
        when(reservationRepository.save(any(PackageReservation.class))).thenReturn(mockReservation);

        // 2. When (실행)
//        Long resultId = reservationService.register(dto);

        // 3. Then (검증)
//        assertThat(resultId).isEqualTo(100L);
    }
}