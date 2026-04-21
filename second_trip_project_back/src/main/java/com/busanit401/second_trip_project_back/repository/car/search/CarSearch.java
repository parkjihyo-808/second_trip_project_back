package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CarSearch {

    List<Car> searchCompanyCarsByPage(String carName, String region, List<Long> unavailableIds, Pageable pageable);

    int searchCountByCarNameAndRegion(String carName, String region, List<Long> unavailableIds);

    List<CarTypeDTO> searchCarTypesByRegionWithCursor(String region, List<Long> unavailableIds, int cursorPrice, String cursorName, Pageable pageable);

    List<Car> searchOptionsByCarNamesIn(List<String> carNames, String region, List<Long> unavailableIds);
}