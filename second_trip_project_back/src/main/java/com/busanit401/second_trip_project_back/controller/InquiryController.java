package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.InquiryDTO;
import com.busanit401.second_trip_project_back.entity.Inquiry;
import com.busanit401.second_trip_project_back.repository.InquiryRepository;
import com.busanit401.second_trip_project_back.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/inquiries")
@RequiredArgsConstructor
public class InquiryController {
    private final InquiryRepository inquiryRepository;
    private final InquiryService inquiryService; // 👈 1. 서비스 객체 주입 선언 (소문자 시작)

    // 특정 유저의 문의 내역만 조회
    @GetMapping
    public ResponseEntity<List<InquiryDTO>> getInquiryList(Principal principal) {
        // 1. principal 확인
        if (principal == null) {
            return ResponseEntity.status(401).build(); // 로그인 안 됨
        }

        String mid = principal.getName();
        System.out.println(">>> 조회 요청 사용자: " + mid); // 로그 확인

        // 2. 💡 InquiryService(클래스명)가 아니라 주입받은 inquiryService(객체명)를 사용
        List<InquiryDTO> list = inquiryService.getList(mid);

        return ResponseEntity.ok(list);
    }

    // 문의글 작성
    @PostMapping
    public ResponseEntity<?> register(@RequestBody Inquiry inquiry, Principal principal) { // Principal 추가
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 💡 프론트에서 넘어온 객체에 로그인한 유저의 이메일(mid)을 강제로 덮어씌움
        Inquiry inquiryToSave = Inquiry.builder()
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .category(inquiry.getCategory())
                .mid(principal.getName()) // 로그인한 유저 ID 사용
                .build();

        Inquiry saved = inquiryRepository.save(inquiryToSave);
        return ResponseEntity.ok(saved);
    }

    }
