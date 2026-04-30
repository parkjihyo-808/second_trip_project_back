package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.packages.TravelPackageItem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TravelPackageItemRepository extends JpaRepository<TravelPackageItem, Long> {
    Page<TravelPackageItem> findByCategory(String category, Pageable pageable);
}