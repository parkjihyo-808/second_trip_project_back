package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.dto.PackageReservationDTO;

import java.util.List;

public interface PackageReservationService {
    //예약 등록
    Long register(PackageReservationDTO packageReservationDto, String email);
    //예약 목록 조회
    List<PackageReservationDTO> getMyPackageReservations(String email);
    //예약 삭제
    void deleteReservation(Long reservationId, String email);
}