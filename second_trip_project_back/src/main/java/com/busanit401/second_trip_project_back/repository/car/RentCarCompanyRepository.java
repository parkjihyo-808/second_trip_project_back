package com.busanit401.second_trip_project_back.repository.car;

import com.busanit401.second_trip_project_back.domain.car.RentCarCompany;
import com.busanit401.second_trip_project_back.repository.car.search.RentCarCompanySearch;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentCarCompanyRepository extends JpaRepository<RentCarCompany, Long>, RentCarCompanySearch {
}