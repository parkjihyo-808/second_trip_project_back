package com.busanit401.second_trip_project_back.domain.packages;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "trip_package_item") // 테이블명
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class TravelPackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 고유 ID

    @Column(nullable = false)
    private String title; // 패키지 이름

    @Column(nullable = false)
    private Long price; // 가격

    @JdbcTypeCode(SqlTypes.JSON) // 중요! List<String>도 JSON으로 처리
    @Column(columnDefinition = "json")
    private List<String> inclusions; // 포함사항

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> exclusions; //불포함 사항 (예: 중식, 개인경비)

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<String> tags;  // 예: "#제주여행,#힐링"

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private List<Map<String, Object>> itinerary; //여행 일정

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json")
    private Map<String, Object> flightInfo;

    @Column
    private String thumbnail; // 썸네일 이미지 URL

    @Column
    private Integer minPeople; // 최소 인원

    @Column
    private Integer maxPeople; // 최대 인원

    @Column
    private String category; // 카테고리 (예: Season, Family 등)

    @Column
    private String region; // 여행 지역 (예: 경주, 제주 등)

    @Column(length = 2000)
    private String description; // 패키지에 대한 간단한 설명


    // 필요한 경우 상품 정보 수정 메서드 추가
    public void changePrice(Long price) { this.price = price; }
    public void changeTitle(String title) { this.title = title; }
}