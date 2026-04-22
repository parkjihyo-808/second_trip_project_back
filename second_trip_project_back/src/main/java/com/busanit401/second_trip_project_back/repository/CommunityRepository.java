package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.entity.Community;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
}