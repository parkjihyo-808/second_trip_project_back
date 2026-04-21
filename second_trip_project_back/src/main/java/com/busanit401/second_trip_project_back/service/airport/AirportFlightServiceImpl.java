package com.busanit401.second_trip_project_back.service.airport;

import com.busanit401.second_trip_project_back.dto.airport.AirportFlightDTO;
import com.busanit401.second_trip_project_back.entity.airport.AirportFlight;
import com.busanit401.second_trip_project_back.repository.airport.AirportFlightRepository;
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
public class AirportFlightServiceImpl implements AirportFlightService {

    private final AirportFlightRepository airportFlightRepository;

    // ── 항공편 목록 조회 ─────────────────────────────────────
    // Flutter 검색 시 호출
    // depPlandTime: 앞 8자리(YYYYMMDD) 로 StartingWith 검색
    @Override
    public List<AirportFlightDTO> getFlightList(
            String depAirportId,
            String arrAirportId,
            String depPlandTime) {

        log.info("✅ [AirportFlightService] 항공편 목록 조회 → " +
                        "출발: {} / 도착: {} / 날짜: {}",
                depAirportId, arrAirportId, depPlandTime);

        List<AirportFlight> flights =
                airportFlightRepository
                        .findByDepAirportIdAndArrAirportIdAndDepPlandTimeStartingWith(
                                depAirportId,
                                arrAirportId,
                                depPlandTime
                        );

        log.info("✅ [AirportFlightService] 조회 결과: {}건", flights.size());

        return flights.stream()
                .map(AirportFlightDTO::fromEntity)
                .filter(dto -> dto.getDepPlandTime().compareTo(
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                ) > 0)
                .collect(Collectors.toList());
    }

    // ── 항공편 단건 조회 ─────────────────────────────────────
    // id 로 단건 조회, 없으면 RuntimeException
    @Override
    public AirportFlightDTO getFlight(Long id) {
        log.info("✅ [AirportFlightService] 항공편 단건 조회 → id: {}", id);

        AirportFlight flight = airportFlightRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("❌ [AirportFlightService] 항공편 없음 → id: {}", id);
                    return new RuntimeException("항공편을 찾을 수 없습니다. id: " + id);
                });

        return AirportFlightDTO.fromEntity(flight);
    }

    // ── 항공편 등록 (관리자) ──────────────────────────────────
    // DTO → Entity 변환 후 저장, 생성된 id 반환
    @Override
    public Long register(AirportFlightDTO dto) {
        log.info("✅ [AirportFlightService] 항공편 등록 → {}", dto);

        AirportFlight flight = dto.toEntity();
        AirportFlight saved  = airportFlightRepository.save(flight);

        log.info("✅ [AirportFlightService] 등록 완료 → id: {}", saved.getId());
        return saved.getId();
    }

    // ── 항공편 수정 (관리자) ──────────────────────────────────
    // id 로 기존 항공편 조회 후 changeFlightInfo() 로 수정
    // ⚠️ dto.getEconomyCharge() → entity.price 로 전달 (필드명 불일치, 작동 정상)
    @Override
    public void modify(AirportFlightDTO dto) {
        log.info("✅ [AirportFlightService] 항공편 수정 → id: {}", dto.getId());

        AirportFlight flight = airportFlightRepository.findById(dto.getId())
                .orElseThrow(() -> {
                    log.error("❌ [AirportFlightService] 항공편 없음 → id: {}", dto.getId());
                    return new RuntimeException("항공편을 찾을 수 없습니다. id: " + dto.getId());
                });

        // ⚠️ economyCharge(DTO) → price(Entity) 매핑 (발표 후 통일 예정)
        flight.changeFlightInfo(
                dto.getAirlineNm(),
                dto.getFlightNo(),
                dto.getDepPlandTime(),
                dto.getArrPlandTime(),
                dto.getEconomyCharge(),
                dto.getSeatsLeft()
        );

        airportFlightRepository.save(flight);
        log.info("✅ [AirportFlightService] 수정 완료 → id: {}", dto.getId());
    }

    // ── 항공편 삭제 (관리자) ──────────────────────────────────
    // id 존재 여부 확인 후 삭제
    @Override
    public void remove(Long id) {
        log.info("✅ [AirportFlightService] 항공편 삭제 → id: {}", id);

        if (!airportFlightRepository.existsById(id)) {
            log.error("❌ [AirportFlightService] 항공편 없음 → id: {}", id);
            throw new RuntimeException("항공편을 찾을 수 없습니다. id: " + id);
        }

        airportFlightRepository.deleteById(id);
        log.info("✅ [AirportFlightService] 삭제 완료 → id: {}", id);
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.service.airport.AirportFlightServiceImpl
 * 역할  : 항공편 비즈니스 로직 (조회/등록/수정/삭제)
 * 사용처 : AirportFlightController
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportFlightRepository.java  : DB 조회/저장/삭제
 * - AirportFlightDTO.java         : 입출력 DTO
 * - AirportFlight.java            : 엔티티 (changeFlightInfo 메서드)
 * - AirportFlightService.java     : 인터페이스
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 기본 CRUD 구현
 * ----------------------------------------------------------------------------------
 * [메서드 목록]
 * - getFlightList(depAirportId, arrAirportId, depPlandTime) : 항공편 목록 조회
 * - getFlight(id)    : 항공편 단건 조회
 * - register(dto)    : 항공편 등록
 * - modify(dto)      : 항공편 수정 (changeFlightInfo 호출)
 * - remove(id)       : 항공편 삭제
 * ----------------------------------------------------------------------------------
 * [파일 흐름과 순서]
 * [조회] Flutter GET → Controller → getFlightList() → Repository → DTO 변환 → 반환
 * [등록] Flutter POST → Controller → register() → toEntity() → save() → id 반환
 * [수정] Flutter PUT → Controller → modify() → findById() → changeFlightInfo() → save()
 * [삭제] Flutter DELETE → Controller → remove() → existsById() → deleteById()
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ 필드명 불일치 (발표 후 통일 예정)
 *    modify() 에서 dto.getEconomyCharge() → entity.price 로 전달
 *    Entity: price / DTO: economyCharge / Flutter: json['economyCharge']
 * ==================================================================================
 */