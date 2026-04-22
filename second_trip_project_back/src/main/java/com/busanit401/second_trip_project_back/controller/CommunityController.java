package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.entity.Community;
import com.busanit401.second_trip_project_back.service.CommunityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*; // 👈 @GetMapping 사용을 위해 수정

import java.util.List; // 👈 List 사용을 위해 추가

@RestController
@RequestMapping("/community")
@RequiredArgsConstructor
public class CommunityController {
    private final CommunityService communityService;

    @PostMapping("/register")
    public String register(@RequestBody Community community) {
        communityService.register(community);
        return "success";
    }

    // 💡 목록 조회 추가
    @GetMapping("/list")
    public List<Community> list() {
        return communityService.findAll();
    }
}