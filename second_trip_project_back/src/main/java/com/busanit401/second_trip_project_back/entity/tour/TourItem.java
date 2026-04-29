package com.busanit401.second_trip_project_back.entity.tour;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "trip_tour_item")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //고유 식별자(PK)
    private Long tourId;

    @Column(nullable = false)
    //투어 제목
    private String title;

    @Column(columnDefinition = "TEXT")
    //투어 상세 설명
    private String description;

    @Column(columnDefinition = "TEXT")
    //투어 요약
    private String subDescription;

    //지역 정보
    private String location;

    //투어 가격
    private Long tourPrice;

    //식사 추가 시 추가금
    private Long mealPrice;

    //썸네일 이미지
    private String imageUrl;

    //관련 웹사이트 또는 정보 출처 URL
    private String reference;

    //데이터 생성 일시
    private LocalDateTime createdAt;

    // 투어:식당 = 1:N
    @OneToMany(mappedBy = "tourItem", cascade = CascadeType.ALL)
    private List<TourRestaurant> restaurants = new ArrayList<>();
}