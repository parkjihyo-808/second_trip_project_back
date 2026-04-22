package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.entity.Community;
import com.busanit401.second_trip_project_back.repository.CommunityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List; // 👈 이 import가 꼭 있어야 합니다!

@Service
@RequiredArgsConstructor
public class CommunityService {

    private final CommunityRepository communityRepository;

    // 글 등록
    public void register(Community community) {
        communityRepository.save(community);
    }

    // 👈 문제의 findAll() 메서드 (이렇게 생겼는지 확인하세요!)
    public List<Community> findAll() {
        return communityRepository.findAll();
    }
}