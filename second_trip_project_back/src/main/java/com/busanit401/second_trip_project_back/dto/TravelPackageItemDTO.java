package com.busanit401.second_trip_project_back.dto;

import lombok.*;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TravelPackageItemDTO {

    private Long id;                // 상품 고유번호
    private String category;        // 분류
    private String title;           // 상품명
    private String description;     // 소개글
    private String region;          // 지역
    private String thumbnail;       // 썸네일 이미지 URL
    private Long price;             // 가격
    private List<String> tags;      // 태그 리스트
    private Integer minPeople;      // 최소 인원
    private Integer maxPeople;      // 최대 인원

    // --- 상세 화면용 데이터 ---
    private List<String> inclusions;             // 포함 사항
    private List<String> exclusions;             // 불포함 사항
    private Map<String, Object> flightInfo;      // 항공 정보
    private List<Map<String, Object>> itinerary; // 1~n일차별 상세 일정
}