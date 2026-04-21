package com.busanit401.second_trip_project_back.service.review;

import com.busanit401.second_trip_project_back.dto.ReviewDTO;
import java.util.List;

public interface ReviewService {

    // 1. 리뷰 등록 (플러터 '리뷰 쓰기' 기능)
    Long register(ReviewDTO reviewDTO);

    // 2. 내 리뷰 목록 조회 (마이페이지용 - 작성자 아이디로 필터링)
    List<ReviewDTO> getMyReviews(String writer);

    // 3. 특정 리뷰 상세 보기 (필요 시)
    ReviewDTO read(Long rno);

    // 4. 리뷰 수정 (플러터 '리뷰 수정' 기능)
    void modify(ReviewDTO reviewDTO);

    // 5. 리뷰 삭제 (플러터 '리뷰 삭제' 기능)
    void remove(Long rno);

    // 6. 특정 대상(호텔, 렌터카 등)의 전체 리뷰 조회
    List<ReviewDTO> getTargetReviews(String target);
}