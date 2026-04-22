package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.PackageItem;
import com.busanit401.second_trip_project_back.domain.packages.PackageReservation;
import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;
import com.busanit401.second_trip_project_back.enums.ReservationStatus;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.PackageItemRepository;
import com.busanit401.second_trip_project_back.repository.PackageReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class PackageReservationServiceImpl implements PackageReservationService {

    private final MemberRepository memberRepository;
    private final PackageItemRepository packageItemRepository;
    private final PackageReservationRepository reservationRepository;

    @Override
    public Long register(PackageReservationDTO packageReservationDto, String email) {
        Member member = memberRepository.findByMid(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        PackageItem packageItem = packageItemRepository.findById((long) packageReservationDto.getPackageId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 패키지입니다."));

        PackageReservation reservation = PackageReservation.builder()
                .member(member)
                .packageItem(packageItem)
                .reservationDate(packageReservationDto.getReservationDate())
                .peopleCount(packageReservationDto.getPeopleCount())
                .totalPrice(packageReservationDto.getTotalPrice())
                .status(ReservationStatus.PENDING)
                .build();

        return (long) reservationRepository.save(reservation).getId();
    }

    @Override
    public List<PackageReservationDTO> getMyPackageReservations(String email) {
        Member member = memberRepository.findByMid(email)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        return reservationRepository.findByMember(member).stream()
                .map(res -> PackageReservationDTO.builder()
                        .reservationId(res.getId())
                        .packageId(res.getPackageItem().getId()) // ID 매핑
                        .packageName(res.getPackageItem().getTitle())
                        .reservationDate(res.getReservationDate())
                        .peopleCount(res.getPeopleCount())
                        .totalPrice(res.getTotalPrice())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void deleteReservation(Long reservationId, String email) {
        // 1. 예약 정보 조회
        PackageReservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 예약입니다."));

        // 2. 소유권 검증 (중요!)
        if (!reservation.getMember().getMid().equals(email)) {
            throw new IllegalArgumentException("본인의 예약만 삭제할 수 있습니다.");
        }

        // 3. Hard Delete 수행
        reservationRepository.delete(reservation);
    }
}