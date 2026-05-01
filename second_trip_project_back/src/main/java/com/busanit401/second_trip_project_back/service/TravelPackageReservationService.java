package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.TravelPackageItem;
import com.busanit401.second_trip_project_back.domain.packages.TravelPackageReservation;
import com.busanit401.second_trip_project_back.dto.TravelPackageReservationDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.TravelPackageItemRepository;
import com.busanit401.second_trip_project_back.repository.TravelPackageReservationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class TravelPackageReservationService {

    private final TravelPackageReservationRepository reservationRepository;
    private final TravelPackageItemRepository packageItemRepository;
    private final MemberRepository memberRepository;

    public Long register(TravelPackageReservationDTO reservationDTO, String email) {

        // 1. 회원 존재 여부 확인
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("해당 이메일의 회원이 존재하지 않습니다: " + email));

        // 2. 예약 대상 패키지 존재 여부 확인
        TravelPackageItem packageItem = packageItemRepository.findById(reservationDTO.getPackageId())
                .orElseThrow(() -> new EntityNotFoundException("상품 정보를 찾을 수 없습니다. ID: " + reservationDTO.getPackageId()));

        // 3. 엔티티 생성 및 관계 설정 (Builder 패턴 활용)
        TravelPackageReservation reservation = TravelPackageReservation.builder()
                .member(member)
                .travelPackageItem(packageItem)
                .reservationDate(reservationDTO.getReservationDate())
                .peopleCount(reservationDTO.getPeopleCount())
                .totalPrice(reservationDTO.getTotalPrice())
                .build();

        // 4. DB 저장 후 ID 반환
        TravelPackageReservation savedReservation = reservationRepository.save(reservation);

        log.info("예약 완료 처리됨 - 예약ID: {}, 계정: {}", savedReservation.getId(), email);

        return savedReservation.getId();
    }

    @Transactional(readOnly = true)
    public List<TravelPackageReservationDTO> getMyReservations(String email) {
        log.info("내 예약 목록 조회 요청 - 계정: {}", email);

        // Repository에 findByMemberEmail 같은 메서드가 정의되어 있어야 합니다.
        List<TravelPackageReservation> results = reservationRepository.findByMemberEmail(email);

        // Entity 리스트를 DTO 리스트로 변환하여 반환
        return results.stream().map(reservation -> TravelPackageReservationDTO.builder()
                .reservationId(reservation.getId())
                .packageId(reservation.getTravelPackageItem().getId())
                .packageName(reservation.getTravelPackageItem().getTitle()) // 상품명 추가
                .reservationDate(reservation.getReservationDate())
                .peopleCount(reservation.getPeopleCount())
                .totalPrice(reservation.getTotalPrice())
                .build()
        ).collect(Collectors.toList());
    }

    // 3. 예약 취소/삭제 로직 (추가)
    public void remove(Long reservationId) {
        log.info("예약 삭제 요청 - ID: {}", reservationId);
        if (!reservationRepository.existsById(reservationId)) {
            throw new EntityNotFoundException("삭제할 예약 정보를 찾을 수 없습니다. ID: " + reservationId);
        }
        reservationRepository.deleteById(reservationId);
    }
}

/*
 * 예약 정보를 등록합니다.
 프론트에서 날아온 회원 정보와 상품 정보를 조회,
 있으면 예약 정보 DB에 저장
 */