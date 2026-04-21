package com.busanit401.second_trip_project_back.service.airport;

import com.busanit401.second_trip_project_back.dto.airport.AirportPassengerDTO;
import com.busanit401.second_trip_project_back.dto.airport.AirportReservationDTO;
import com.busanit401.second_trip_project_back.entity.airport.AirportPassenger;
import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import com.busanit401.second_trip_project_back.repository.airport.AirportFlightRepository;
import com.busanit401.second_trip_project_back.repository.airport.AirportReservationRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class AirportReservationServiceImpl implements AirportReservationService {

    private final AirportReservationRepository airportReservationRepository;
    private final AirportFlightRepository airportFlightRepository;

    // ── 예약 등록 ─────────────────────────────────────────────
    // 처리 순서:
    //   1. 중복 예약 체크 (같은 항공편 + 탑승객 이름 + 생년월일)
    //   2. 예약 엔티티 저장
    //   3. 탑승객 목록 저장
    //   4. 가는편 잔여석 차감
    //   5. 왕복이면 오는편 잔여석도 차감
    @Override
    @Transactional
    public Long register(AirportReservationDTO dto) {

        log.info("✅ [AirportReservationService] 예약 등록 시작 → mid: {}", dto.getMid());

        // ── 1단계: 중복 예약 체크 ─────────────────────────────
        // 탑승객별로 같은 항공편 + 이름 + 생년월일 조합 중복 확인
        // 중복 시 RuntimeException → Controller 에서 400 응답
        if (dto.getPassengers() != null) {
            for (AirportPassengerDTO passenger : dto.getPassengers()) {
                boolean isDuplicate = airportReservationRepository
                        .existsDuplicateReservation(
                                dto.getFlightNo(),
                                passenger.getPassengerName(),
                                passenger.getPassengerBirth()
                        );
                if (isDuplicate) {
                    log.warn("❌ [AirportReservationService] 중복 예약 감지 → " +
                                    "항공편: {} / 탑승객: {}",
                            dto.getFlightNo(), passenger.getPassengerName());
                    throw new RuntimeException(
                            "이미 예약된 항공편입니다. 탑승객: " + passenger.getPassengerName());
                }
            }
        }

        // ── 2단계: 예약 엔티티 먼저 저장 ─────────────────────
        // passengers 는 toEntity() 에 미포함 → 3단계에서 별도 저장
        AirportReservation reservation = dto.toEntity();
        AirportReservation saved = airportReservationRepository.save(reservation);
        log.info("✅ [AirportReservationService] 예약 저장 완료 → id: {}", saved.getId());

        // ── 3단계: 탑승객 목록 저장 ──────────────────────────
        // saved(저장된 예약) 를 각 탑승객 엔티티에 연결 후 저장
        if (dto.getPassengers() != null && !dto.getPassengers().isEmpty()) {
            for (AirportPassengerDTO passengerDTO : dto.getPassengers()) {
                AirportPassenger passenger = passengerDTO.toEntity(saved);
                saved.getPassengers().add(passenger);
            }
            airportReservationRepository.save(saved);
            log.info("✅ [AirportReservationService] 탑승객 {}명 저장 완료",
                    dto.getPassengers().size());
        }

        // ── 4단계: 가는편 잔여석 차감 ────────────────────────
        // depPlandTime 앞 8자리(YYYYMMDD) + 공항코드 + 항공편명으로 항공편 조회
        // 탑승객 수만큼 seatsLeft 차감 (최소 0석)
        int passengerCount = dto.getPassengers() != null
                ? dto.getPassengers().size() : 1;

        log.info("✅ [AirportReservationService] 가는편 잔여석 차감 시도 → " +
                        "depAirportId: {} / arrAirportId: {} / depPlandTime앞8자리: {}",
                dto.getDepAirportId(), dto.getArrAirportId(),
                dto.getDepPlandTime() != null ? dto.getDepPlandTime().substring(0, 8) : "NULL");

        airportFlightRepository
                .findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
                        dto.getDepAirportId(),
                        dto.getArrAirportId(),
                        dto.getDepPlandTime().substring(0, 8)
                )
                .stream()
                .filter(f -> f.getFlightNo().equals(dto.getFlightNo()))
                .findFirst()
                .ifPresent(flight -> {
                    int updated = Math.max(0, flight.getSeatsLeft() - passengerCount);
                    flight.setSeatsLeft(updated);
                    airportFlightRepository.save(flight);
                    log.info("✅ [AirportReservationService] 가는편 잔여석 차감 완료 → {} → {}석",
                            dto.getFlightNo(), updated);
                });

        // ── 5단계: 오는편 잔여석 차감 (왕복일 때만) ──────────
        // 출발/도착 공항 반전 후 오는편 항공편 조회 후 차감
        if (dto.getRetFlightNo() != null && dto.getRetDepPlandTime() != null) {
            airportFlightRepository
                    .findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
                            dto.getArrAirportId(),
                            dto.getDepAirportId(),
                            dto.getRetDepPlandTime().substring(0, 8)
                    )
                    .stream()
                    .filter(f -> f.getFlightNo().equals(dto.getRetFlightNo()))
                    .findFirst()
                    .ifPresent(flight -> {
                        int updated = Math.max(0, flight.getSeatsLeft() - passengerCount);
                        flight.setSeatsLeft(updated);
                        airportFlightRepository.save(flight);
                        log.info("✅ [AirportReservationService] 오는편 잔여석 차감 완료 → {} → {}석",
                                dto.getRetFlightNo(), updated);
                    });
        }

        log.info("✅ [AirportReservationService] 예약 등록 전체 완료 → id: {}", saved.getId());
        return saved.getId();
    }

    // ── 예약 단건 조회 ────────────────────────────────────────
    // id 로 단건 조회, 없으면 RuntimeException
    @Override
    public AirportReservationDTO getReservation(Long id) {
        log.info("✅ [AirportReservationService] 예약 단건 조회 → id: {}", id);

        AirportReservation reservation = airportReservationRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ [AirportReservationService] 예약 없음 → id: {}", id);
                    return new RuntimeException("예약을 찾을 수 없습니다. id: " + id);
                });

        return AirportReservationDTO.fromEntity(reservation);
    }

    // ── 전체 예약 목록 조회 ───────────────────────────────────
    // 관리자 기능 또는 전체 조회 시 사용 (현재 미사용)
    @Override
    public List<AirportReservationDTO> getReservationList() {
        log.info("✅ [AirportReservationService] 전체 예약 목록 조회");

        List<AirportReservation> list =
                airportReservationRepository.findAllByOrderByReservedAtDesc();

        log.info("✅ [AirportReservationService] 전체 조회 완료 → {}건", list.size());

        return list.stream()
                .map(AirportReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── 회원 ID로 예약 목록 조회 ─────────────────────────────
    // MyReservationScreen 진입 시 호출
    // 정렬 순서: 예약완료(미래 항공편) → 지난예약(과거 항공편), 각 그룹 내 최신순
    // now: 현재 시각을 YYYYMMDDHHMMSS 형식으로 변환해 JPQL 에 전달
    @Override
    public List<AirportReservationDTO> getReservationListByMid(String mid) {
        log.info("✅ [AirportReservationService] mid로 예약 조회 → {}", mid);

        // 현재 시각 문자열 생성 (depPlandTime 형식과 동일: YYYYMMDDHHMMSS)
        String now = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        List<AirportReservation> list =
                airportReservationRepository.findByMidSorted(mid, now);

        log.info("✅ [AirportReservationService] mid 조회 완료 → {}건", list.size());

        return list.stream()
                .map(AirportReservationDTO::fromEntity)
                .collect(Collectors.toList());
    }

    // ── 예약 취소 (삭제) ──────────────────────────────────────
    // id 존재 여부 확인 후 삭제
    // CascadeType.ALL 로 탑승객(AirportPassenger) 도 함께 삭제됨
    @Override
    public void remove(Long id) {
        log.info("✅ [AirportReservationService] 예약 취소 시작 → id: {}", id);

        if (!airportReservationRepository.existsById(id)) {
            log.error("❌ [AirportReservationService] 예약 없음 → id: {}", id);
            throw new RuntimeException("예약을 찾을 수 없습니다. id: " + id);
        }

        airportReservationRepository.deleteById(id);
        log.info("✅ [AirportReservationService] 예약 취소 완료 → id: {}", id);
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.service.airport.AirportReservationServiceImpl
 * 역할  : 항공 예약 비즈니스 로직 (등록/조회/취소)
 * 사용처 : AirportReservationController
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservationRepository.java  : 예약 DB 조회/저장/삭제
 * - AirportFlightRepository.java       : 잔여석 차감을 위한 항공편 조회
 * - AirportReservationDTO.java         : 입출력 DTO
 * - AirportPassengerDTO.java           : 탑승객 저장 시 toEntity() 사용
 * - AirportReservationService.java     : 인터페이스
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 구조, 중복 체크 없음
 * - 변경       : 탑승객 다중 저장 구조로 전환 (List<AirportPassengerDTO>)
 *               중복 예약 체크 추가 (existsDuplicateReservation JPQL)
 *               가는편/오는편 잔여석 차감 로직 추가
 *               getReservationListByMid() 정렬 변경
 *               → findByMidOrderByReservedAtDesc → findByMidSorted
 *               → 예약완료(미래) 먼저, 지난예약(과거) 나중 정렬
 * ----------------------------------------------------------------------------------
 * [메서드 목록]
 * - register(dto)                : 예약 등록 (중복체크 → 저장 → 탑승객 → 좌석차감)
 * - getReservation(id)           : 예약 단건 조회
 * - getReservationList()         : 전체 예약 조회 (현재 미사용)
 * - getReservationListByMid(mid) : 회원 예약 목록 조회 (정렬 적용)
 * - remove(id)                   : 예약 취소 (탑승객 Cascade 삭제)
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * [등록]
 * 1. Flutter POST → AirportReservationController → register(dto)
 * 2. 탑승객별 중복 체크 → 중복 시 RuntimeException (400 응답)
 * 3. 예약 저장 → 탑승객 저장 → 가는편 잔여석 차감 → 오는편 차감(왕복)
 *
 * [조회]
 * 1. Flutter GET → AirportReservationController → getReservationListByMid(mid)
 * 2. now(현재시각 문자열) 생성 → findByMidSorted(mid, now)
 * 3. 미래 항공편(예약완료) 먼저, 과거(지난예약) 나중 정렬 후 반환
 *
 * [취소]
 * 1. Flutter DELETE → AirportReservationController → remove(id)
 * 2. existsById 확인 → deleteById → CascadeType.ALL 로 탑승객 함께 삭제
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * - 잔여석 차감 시 depPlandTime 앞 8자리(YYYYMMDD) 로 검색
 *   → flightNo 로 최종 필터링
 * - 오는편 차감 시 출발/도착 공항코드 반전 주의 (arrAirportId ↔ depAirportId)
 * - now 형식: yyyyMMddHHmmss (depPlandTime 과 동일 형식으로 문자열 비교)
 * ==================================================================================
 */