package com.busanit401.second_trip_project_back.service.review;

import com.busanit401.second_trip_project_back.domain.review.Review;
import com.busanit401.second_trip_project_back.dto.ReviewDTO;
import com.busanit401.second_trip_project_back.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional // 메서드가 끝날 때 자동으로 DB 반영!
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public Long register(ReviewDTO reviewDTO) {
        log.info("리뷰 등록 로직 실행: " + reviewDTO);

        Review review = Review.builder()
                .target(reviewDTO.getTarget())
                .category(reviewDTO.getCategory())
                .rating(reviewDTO.getRating())
                .content(reviewDTO.getContent())
                .writer(reviewDTO.getWriter())
                .reviewImg(reviewDTO.getReviewImg())
                .build();

        return reviewRepository.save(review).getRno();
    }

    @Override
    public List<ReviewDTO> getMyReviews(String writer) {
        log.info("내 리뷰 목록 조회 (작성자): " + writer);

        List<Review> result = reviewRepository.findByWriterOrderByRnoDesc(writer);

        // 엔티티 리스트를 DTO 리스트로 변환해서 반환
        return result.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewDTO read(Long rno) {
        Optional<Review> result = reviewRepository.findById(rno);
        Review review = result.orElseThrow(() -> new RuntimeException("해당 리뷰가 존재하지 않습니다."));
        return entityToDTO(review);
    }

    @Override
    public void modify(ReviewDTO reviewDTO) {
        log.info("리뷰 수정 로직 실행: " + reviewDTO);

        Optional<Review> result = reviewRepository.findById(reviewDTO.getRno());
        Review review = result.orElseThrow(() -> new RuntimeException("해당 리뷰를 찾을 수 없습니다."));

        // 엔티티의 비즈니스 메서드 호출 (Member와 동일한 방식!)
        review.changeContent(reviewDTO.getContent());
        review.changeRating(reviewDTO.getRating());
        review.changeReviewImg(reviewDTO.getReviewImg());

        // @Transactional 덕분에 따로 save 안 해도 되지만, 명시적으로 작성
        reviewRepository.save(review);
    }

    @Override
    public void remove(Long rno) {
        log.info("리뷰 삭제 로직 실행: " + rno);
        reviewRepository.deleteById(rno);
    }

    @Override
    public List<ReviewDTO> getTargetReviews(String target) {
        List<Review> result = reviewRepository.findByTargetOrderByRnoDesc(target);
        return result.stream()
                .map(this::entityToDTO)
                .collect(Collectors.toList());
    }

    private ReviewDTO entityToDTO(Review review) {
        return ReviewDTO.builder()
                .rno(review.getRno())
                .target(review.getTarget())
                .category(review.getCategory())
                .rating(review.getRating())
                .content(review.getContent())
                .writer(review.getWriter())
                .reviewImg(review.getReviewImg())
                .regDate(review.getRegDate())
                .build();
    }
}