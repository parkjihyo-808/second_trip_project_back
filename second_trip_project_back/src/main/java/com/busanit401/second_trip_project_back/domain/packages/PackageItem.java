package com.busanit401.second_trip_project_back.domain.packages;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_package_item") // 테이블명
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class PackageItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 상품 고유 ID

    @Column(nullable = false)
    private String title; // 패키지 이름

    @Column(nullable = false)
    private int price; // 가격

    @Column
    private String thumbnail; // 썸네일 이미지 URL

    @Column(length = 1000)
    private String inclusions; // 포함사항 (여러 항목을 쉼표로 구분하여 저장하거나 추후 별도 테이블로 분리 가능)

    @Column(columnDefinition = "TEXT")
    private String itinerary; // 여행 일정 (JSON 문자열로 저장하거나, 별도 엔티티 구성 권장)

    @Column
    private int minPeople; // 최소 인원

    @Column
    private int maxPeople; // 최대 인원

    // 필요한 경우 상품 정보 수정 메서드 추가
    public void changePrice(int price) { this.price = price; }
    public void changeTitle(String title) { this.title = title; }
}