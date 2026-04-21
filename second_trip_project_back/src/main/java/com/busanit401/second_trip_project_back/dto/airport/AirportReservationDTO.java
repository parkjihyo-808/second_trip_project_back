package com.busanit401.second_trip_project_back.dto.airport;

import com.busanit401.second_trip_project_back.entity.airport.AirportReservation;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

// 항공 예약 DTO - Entity ↔ Flutter 간 데이터 전달 객체
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AirportReservationDTO {

    // ── 기본키 ──────────────────────────────────────────────
    private Long id;

    // ── 회원 정보 ────────────────────────────────────────────
    private String mid;             // 로그인 회원 ID

    // ── 가는편 정보 ──────────────────────────────────────────
    private String airlineNm;       // 항공사명
    private String flightNo;        // 항공편명 (예: KE1234)
    private String depAirportNm;    // 출발 공항명
    private String arrAirportNm;    // 도착 공항명
    private String depAirportId;    // 출발 공항코드 (예: GIMHAE)
    private String arrAirportId;    // 도착 공항코드 (예: JEJU)
    private String depPlandTime;    // 출발 예정시각 (예: 20260501134000)
    private String arrPlandTime;    // 도착 예정시각
    private Integer depPrice;       // 가는편 가격

    // ── 오는편 정보 (왕복일 때만 값 있음, 편도일 때 null) ────
    private String retAirlineNm;    // 오는편 항공사명
    private String retFlightNo;     // 오는편 항공편명
    private String retDepPlandTime; // 오는편 출발 예정시각
    private String retArrPlandTime; // 오는편 도착 예정시각
    private Integer retPrice;       // 오는편 가격

    // ── 탑승객 목록 ──────────────────────────────────────────
    // AirportPassenger 테이블과 1:N 관계
    private List<AirportPassengerDTO> passengers;

    // ── 예약 메타 정보 ───────────────────────────────────────
    private Boolean isRoundTrip;    // 왕복 여부
    private String reservedAt;      // 예약 일시

    // ── 총 금액 계산 ─────────────────────────────────────────
    // ⚠️ [주의] Flutter ReservationItem.totalPrice 와 계산 방식 다름
    // 서버: 단순 합산 (소아/유아 차등 없음)
    // Flutter: 소아 75% / 유아 10% 차등 계산
    // 현재 어디서 호출되는지 확인 필요 → 미사용이면 추후 제거 예정
    public int getTotalPrice() {
        final int fee = 1000;
        if (Boolean.TRUE.equals(isRoundTrip) && retPrice != null) {
            return depPrice + retPrice + fee;
        }
        return depPrice + fee;
    }

    // ── Entity → DTO 변환 ────────────────────────────────────
    // 서버 → Flutter 응답 시 사용 (예약 목록 조회, 예약 등록 후 반환)
    // passengers: stream 으로 AirportPassengerDTO 리스트 변환
    public static AirportReservationDTO fromEntity(AirportReservation entity) {
        return AirportReservationDTO.builder()
                .id(entity.getId())
                .mid(entity.getMid())
                .airlineNm(entity.getAirlineNm())
                .flightNo(entity.getFlightNo())
                .depAirportNm(entity.getDepAirportNm())
                .arrAirportNm(entity.getArrAirportNm())
                .depAirportId(entity.getDepAirportId())
                .arrAirportId(entity.getArrAirportId())
                .depPlandTime(entity.getDepPlandTime())
                .arrPlandTime(entity.getArrPlandTime())
                .depPrice(entity.getDepPrice())
                .retAirlineNm(entity.getRetAirlineNm())
                .retFlightNo(entity.getRetFlightNo())
                .retDepPlandTime(entity.getRetDepPlandTime())
                .retArrPlandTime(entity.getRetArrPlandTime())
                .retPrice(entity.getRetPrice())
                .passengers(entity.getPassengers().stream()
                        .map(AirportPassengerDTO::fromEntity)
                        .collect(Collectors.toList()))
                .isRoundTrip(entity.getIsRoundTrip())
                .reservedAt(entity.getReservedAt())
                .build();
    }

    // ── DTO → Entity 변환 ────────────────────────────────────
    // Flutter → 서버 예약 등록 요청 시 사용
    // passengers 는 별도로 AirportReservationServiceImpl 에서 처리
    // (reservation 저장 후 각 passenger 에 reservation 참조 연결)
    public AirportReservation toEntity() {
        return AirportReservation.builder()
                .mid(mid)
                .airlineNm(airlineNm)
                .flightNo(flightNo)
                .depAirportNm(depAirportNm)
                .arrAirportNm(arrAirportNm)
                .depAirportId(depAirportId)
                .arrAirportId(arrAirportId)
                .depPlandTime(depPlandTime)
                .arrPlandTime(arrPlandTime)
                .depPrice(depPrice)
                .retAirlineNm(retAirlineNm)
                .retFlightNo(retFlightNo)
                .retDepPlandTime(retDepPlandTime)
                .retArrPlandTime(retArrPlandTime)
                .retPrice(retPrice)
                .isRoundTrip(isRoundTrip)
                .reservedAt(reservedAt)
                .build();
    }
}

/*
 * ==================================================================================
 * [파일 정보]
 * 위치  : com.busanit401.second_trip_project_back.dto.airport.AirportReservationDTO
 * 역할  : 항공 예약 데이터 전달 객체 (Entity ↔ Flutter 간 변환)
 * 사용처 : AirportReservationController, AirportReservationServiceImpl
 * ----------------------------------------------------------------------------------
 * [연관 파일]
 * - AirportReservation.java            : 변환 대상 엔티티
 * - AirportPassengerDTO.java           : passengers 리스트 변환에 사용
 * - AirportReservationServiceImpl.java : fromEntity() / toEntity() 호출
 * - AirportReservationController.java  : 요청/응답 DTO 로 사용
 * - reservation_item.dart             : Flutter 에서 JSON 파싱 기준
 * ----------------------------------------------------------------------------------
 * [변경 이력]
 * - 최초 작성 : 단일 탑승객 필드 구조
 * - 변경       : 탑승객 List<AirportPassengerDTO> 로 확장
 *               오는편(ret*) 필드 추가 (왕복 지원)
 *               getTotalPrice() 추가 (단순 합산 방식)
 * ----------------------------------------------------------------------------------
 * [주의사항 / 참고]
 * ⚠️ getTotalPrice() 계산 방식 불일치
 *    서버: 단순 합산 (소아/유아 차등 없음)
 *    Flutter: 소아 75% / 유아 10% 차등 계산
 *    → 현재 호출 여부 확인 필요, 미사용이면 추후 제거 예정
 *
 * - toEntity() 에서 passengers 미포함 → ServiceImpl 에서 별도 저장 처리
 * - 오는편 필드(ret*) 는 편도일 때 null → Flutter 에서 null 허용으로 처리
 * ==================================================================================
 */