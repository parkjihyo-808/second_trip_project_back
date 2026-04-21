package com.busanit401.second_trip_project_back.dto.car;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CarSearchCursorResponseDTO {
    private List<CarSearchResultDTO> content;
    private boolean hasNext;
    private Integer nextCursorPrice;
    private String nextCursorName;
}