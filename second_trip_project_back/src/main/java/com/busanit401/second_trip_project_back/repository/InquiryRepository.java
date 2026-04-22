package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
// InquiryRepository.java
public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    // 이 메서드 이름을 정확히 리포지토리에 추가하세요
    List<Inquiry> findByMidOrderByRegDateDesc(String mid);
}