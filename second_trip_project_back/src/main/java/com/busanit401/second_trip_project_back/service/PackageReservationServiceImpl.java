package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.PackageItem;
import com.busanit401.second_trip_project_back.domain.packages.PackageReservation;
import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.PackageItemRepository;
import com.busanit401.second_trip_project_back.repository.PackageReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class PackageReservationServiceImpl implements PackageReservationService {

    private final MemberRepository memberRepository;
    private final PackageItemRepository packageItemRepository;
    private final PackageReservationRepository reservationRepository;

    @Override
    public Long register(PackageReservationDTO packageReservationDto) {
        // 1. JWT 토큰(또는 SecurityContext)에서 이메일(mid) 추출
        // (이미 로그인 필터에서 인증했으므로 SecurityContext에서 가져오면 됩니다)
        String mid = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. 이메일로 회원 정보 조회
        Member member = memberRepository.findByMid(mid) // 이 메서드가 없다면 Repository에 추가하세요
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 3. 패키지 정보 조회
        PackageItem packageItem = packageItemRepository.findById(packageReservationDto.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 패키지입니다."));

        // 4. 예약 생성 (조회한 member 객체와 DTO의 예약 정보를 합쳐서 생성)
        PackageReservation reservation = PackageReservation.builder()
                .member(member)                                         // 서버가 조회한 Member 객체
                .packageItem(packageItem)                               // 서버가 조회한 PackageItem 객체
                .reservationDate(packageReservationDto.getReservationDate()) // DTO의 예약 날짜
                .peopleCount(packageReservationDto.getPeopleCount())         // DTO의 인원수
                .totalPrice(packageReservationDto.getTotalPrice())           // DTO의 총 금액
                .build();

        return reservationRepository.save(reservation).getId();
    }
}