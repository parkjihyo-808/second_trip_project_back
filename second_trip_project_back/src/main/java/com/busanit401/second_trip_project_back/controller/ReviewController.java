package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.ReviewDTO;
import com.busanit401.second_trip_project_back.service.review.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequiredArgsConstructor
@RequestMapping("/api/review")
public class ReviewController {

    private final ReviewService reviewService;

    // 1. 리뷰 등록 (POST)
    @PostMapping("/register")
    public ResponseEntity<Long> register(@RequestBody ReviewDTO reviewDTO) {
        log.info("리뷰 등록 요청! 데이터: " + reviewDTO);
        Long rno = reviewService.register(reviewDTO);
        return ResponseEntity.ok(rno);
    }

    // 2. 내 리뷰 목록 조회 (GET) - 마이페이지용
    @GetMapping("/my/{writer}")
    public List<ReviewDTO> getMyReviews(@PathVariable("writer") String writer) {
        log.info("내 리뷰 목록 조회 요청! 작성자: " + writer);
        return reviewService.getMyReviews(writer);
    }

    // 3. 리뷰 상세 조회 (GET)
    @GetMapping("/{rno}")
    public ReviewDTO read(@PathVariable("rno") Long rno) {
        log.info("리뷰 상세 조회! 번호: " + rno);
        return reviewService.read(rno);
    }

    // 4. 리뷰 수정 (PUT)
    @PutMapping("/modify")
    public ResponseEntity<String> modify(@RequestBody ReviewDTO reviewDTO) {
        log.info("리뷰 수정 요청! 수정 데이터: " + reviewDTO);
        reviewService.modify(reviewDTO);
        return ResponseEntity.ok("success");
    }

    // 5. 리뷰 삭제 (DELETE)
    @DeleteMapping("/{rno}")
    public ResponseEntity<String> remove(@PathVariable("rno") Long rno) {
        log.info("리뷰 삭제 요청! 번호: " + rno);
        reviewService.remove(rno);
        return ResponseEntity.ok("success");
    }
}