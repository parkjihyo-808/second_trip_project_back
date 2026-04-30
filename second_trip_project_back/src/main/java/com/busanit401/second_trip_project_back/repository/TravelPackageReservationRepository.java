package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.packages.TravelPackageReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TravelPackageReservationRepository extends JpaRepository<TravelPackageReservation, Long> {
    List<TravelPackageReservation> findByMember(Member member);
}