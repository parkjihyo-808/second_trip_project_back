package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.PackageReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PackageReservationRepository extends JpaRepository<PackageReservation, Long> {
    List<PackageReservation> findByMember(Member member);
}