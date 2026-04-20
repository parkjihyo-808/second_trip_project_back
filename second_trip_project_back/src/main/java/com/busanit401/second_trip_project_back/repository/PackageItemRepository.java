package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.packages.PackageItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PackageItemRepository extends JpaRepository<PackageItem, Long> {
}