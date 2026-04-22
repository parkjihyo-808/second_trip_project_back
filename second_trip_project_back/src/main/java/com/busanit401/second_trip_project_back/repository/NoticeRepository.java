package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByIdDesc();
}