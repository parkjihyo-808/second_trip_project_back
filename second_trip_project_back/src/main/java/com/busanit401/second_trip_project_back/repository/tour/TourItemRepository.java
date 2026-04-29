package com.busanit401.second_trip_project_back.repository.tour;

import com.busanit401.second_trip_project_back.entity.tour.TourItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TourItemRepository extends JpaRepository<TourItem, Long> {
    //JpaRepository가 페이징에 필요한 기능을 지원함(Page<TourItem> findAll(Pageable pageable))
}