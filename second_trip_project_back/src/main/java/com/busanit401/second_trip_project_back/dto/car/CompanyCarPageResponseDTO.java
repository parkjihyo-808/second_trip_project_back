package com.busanit401.second_trip_project_back.dto.car;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CompanyCarPageResponseDTO {
    private List<CarSearchResultDTO.CompanyCarDTO> companyCarDTOs;
    private int page;
    private int size;
    private int totalCount;
    private int totalPages;
    private boolean hasNext;
}