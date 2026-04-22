package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.dto.NoticeDTO;
import com.busanit401.second_trip_project_back.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService service;

    @GetMapping
    public ResponseEntity<List<NoticeDTO>> getAll() {
        return ResponseEntity.ok(service.getAllNotices());
    }

    // 💡 단 하나의 POST 메서드만 유지
    @PostMapping
    public ResponseEntity<NoticeDTO> create(@RequestBody NoticeDTO dto) {
        return ResponseEntity.ok(service.saveNotice(dto));
    }
}