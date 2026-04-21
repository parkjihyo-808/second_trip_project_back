package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.review.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 1. 특정 작성자(아이디)의 모든 리뷰 조회 (최신순) - 마이페이지용
    List<Review> findByWriterOrderByRnoDesc(String writer);

    // 2. 카테고리별 리뷰 조회 (숙소, 항공 등)
    List<Review> findByCategoryOrderByRnoDesc(String category);

    // 3. 특정 대상(target)에 대한 리뷰 조회 (예: 특정 호텔의 리뷰 모아보기)
    List<Review> findByTargetOrderByRnoDesc(String target);

    // 4. 별점 기준 검색 (예: 5점짜리 리뷰만 보기)
    List<Review> findByRating(int rating);

    // 5. 작성자의 리뷰 개수 집계 (마이페이지 상단 요약용)
    long countByWriter(String writer);

    // 6. 대상 이름으로 리뷰 검색 (부분 일치)
    List<Review> findByTargetContaining(String target);

    // 7. 통합 검색 (대상 이름 또는 내용에 키워드가 포함된 리뷰 검색)
    @Query("SELECT r FROM Review r WHERE " +
            "r.target LIKE %:keyword% OR " +
            "r.content LIKE %:keyword%")
    List<Review> searchByKeyword(@Param("keyword") String keyword);

    // 8. 특정 작성자의 카테고리별 리뷰 목록 (마이페이지 상세 필터링용)
    List<Review> findByWriterAndCategoryOrderByRnoDesc(String writer, String category);
}