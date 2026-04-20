package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;

public interface PackageReservationService {
    Long register(PackageReservationDTO packageReservationDto);
}
