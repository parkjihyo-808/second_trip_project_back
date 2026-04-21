package com.busanit401.second_trip_project_back.domain.review;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "trip_review")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long rno; // 리뷰 번호 (Primary Key)

    @Column(nullable = false, length = 100)
    private String target;   // 리뷰 대상 (예: 제주 신라호텔, 아반떼 CN7 등)

    @Column(nullable = false, length = 20)
    private String category; // 카테고리 (숙소, 항공, 렌터카, 패키지)

    @Column(nullable = false)
    private int rating;      // 별점 (1~5)

    @Column(nullable = false, length = 2000)
    private String content;  // 리뷰 내용

    @Column(nullable = false, length = 100)
    private String writer;   // 작성자 아이디 (Member의 mid와 연결될 정보)

    @Column(length = 255)
    private String reviewImg; // 리뷰 이미지 경로 (선택 사항)

    @Column(nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime regDate = LocalDateTime.now(); // 작성일

    // ──────────────────────────────────────────────
    // 비즈니스 메서드 (수정 기능을 위해 추가)
    // ──────────────────────────────────────────────

    // 리뷰 내용 수정
    public void changeContent(String content) {
        this.content = content;
    }

    // 별점 수정
    public void changeRating(int rating) {
        this.rating = rating;
    }

    // 리뷰 이미지 수정
    public void changeReviewImg(String reviewImg) {
        this.reviewImg = reviewImg;
    }
}