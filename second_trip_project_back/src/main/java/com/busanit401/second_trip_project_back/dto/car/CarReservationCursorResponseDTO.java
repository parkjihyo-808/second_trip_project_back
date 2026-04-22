package com.busanit401.second_trip_project_back.dto.car;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CarReservationCursorResponseDTO {
    private List<CarReservationDTO> reservation;
    private boolean hasNext;
    private Integer nextCursorStatusOrder;  // 0=CONFIRMED, 1=나머지
    private String nextCursorStartDate;     // ISO 형식
    private Long nextCursorId;
}