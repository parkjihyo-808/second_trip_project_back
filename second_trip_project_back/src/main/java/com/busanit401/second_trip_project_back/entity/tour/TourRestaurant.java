package com.busanit401.second_trip_project_back.entity.tour;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "trip_tour_restaurant")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourRestaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //고유 식별자(PK)
    private Long restaurantId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private TourItem tourItem; //TourItem과 연관

    @Column(nullable = false)
    //식당 이름
    private String restaurantName;

    //식당 카테고리(한식, 서양식 ...)
    private String category;

    //식당 상세 주소
    private String address;

    //위도
    private Double latitude;
    //경도
    private Double longitude;
}