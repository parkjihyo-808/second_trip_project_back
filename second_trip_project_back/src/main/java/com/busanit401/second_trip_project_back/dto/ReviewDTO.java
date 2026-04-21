package com.busanit401.second_trip_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewDTO {

    private Long rno;         // 리뷰 번호
    private String target;     // 리뷰 대상 (예: 제주 신라호텔)
    private String category;   // 카테고리 (숙소, 항공 등)
    private int rating;        // 별점 (1~5)
    private String content;    // 리뷰 내용
    private String writer;     // 작성자 아이디
    private String reviewImg;  // 리뷰 이미지 경로
    private LocalDateTime regDate; // 작성일

    // ⭐ 나중에 혹시 결과 메시지나 상태를 담아야 할 수도 있어서 비워둔 칸
    private String message;
}