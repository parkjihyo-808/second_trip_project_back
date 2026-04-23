package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;

import java.util.List;

public interface CarSearch {

    List<CarTypeDTO> searchCarTypesByRegionWithCursor(String region, List<Long> unavailableIds, int cursorPrice, String cursorName, int limit);

    List<Car> searchOptionsByCarNamesIn(List<String> carNames, String region, List<Long> unavailableIds);
}