package com.busanit401.second_trip_project_back.service.car;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.domain.car.CarReservation;
import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.dto.car.CarReservationCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationDTO;
import com.busanit401.second_trip_project_back.dto.car.CarReservationRequestDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.repository.car.CarRepository;
import com.busanit401.second_trip_project_back.repository.car.CarReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class RentalServiceImpl implements RentalService {

    private final CarReservationRepository carReservationRepository;
    private final CarRepository carRepository;
    private final MemberRepository userRepository;

    @Override
    @Transactional
    public CarReservationDTO createReservation(String mid, CarReservationRequestDTO request) {
        if (!request.getEndDate().isAfter(request.getStartDate())) {    //빌리는 날짜가 반납날짜보다 먼저인지
            throw new RuntimeException("반납 시간은 대여 시간 이후여야 합니다.");
        }

        Car car = carRepository.findById(request.getCarId())    //차량이 존재하는지
                .orElseThrow(() -> new RuntimeException("차량을 찾을 수 없습니다."));

        if (carReservationRepository.searchExistsOverlap(car.getId(), request.getStartDate(), request.getEndDate())) {  //차량이 해당 날짜로 예약여부
            throw new RuntimeException("해당 시간에 이미 예약된 차량입니다.");
        }

        Member user = userRepository.findByMid(mid) //사용자가 존재하는지
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (carReservationRepository.searchExistsUserOverlap(mid, request.getStartDate(), request.getEndDate())) {  //해당 아이디로 해당 날짜에 차량 상관없이 예약이 있는지
            throw new RuntimeException("해당 시간에 이미 예약된 차가 있습니다.");
        }

        CarReservation carReservation = CarReservation.builder()
                .car(car)
                .user(user)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(CarReservation.RentalStatus.CONFIRMED)
                .build();

        return CarReservationDTO.from(carReservationRepository.save(carReservation));
    }

    @Override
    @Transactional(readOnly = true)
    public CarReservationCursorResponseDTO getMyReservationsCursor(String mid, int cursorStatusOrder, LocalDateTime cursorEndDate, Long cursorId, int size) {
        List<CarReservation> carReservations = carReservationRepository.searchByUserMidWithCursor(
                mid, cursorStatusOrder, cursorEndDate, cursorId, size + 1
        );  //size가 10일 경우 11개의 데이터를 읽어서 가져옴
        boolean hasNext = carReservations.size() > size;    //11개를 가져올수 있기 때문에 10개보다 크면 다음이 있음 이렇게 한 이유는 아래줄에서 10개만 잘라서 표현하기 때문
        List<CarReservation> sliced = hasNext ? carReservations.subList(0, size) : carReservations; //10개의 데이터만 자름

        CarReservation last = hasNext ? sliced.get(sliced.size() - 1) : null;   //마지막 데이터를 커서로 지정
        return CarReservationCursorResponseDTO.builder()
                .reservation(sliced.stream().map(CarReservationDTO::from).toList())
                .hasNext(hasNext)
                .nextCursorStatusOrder(last != null ? (last.getStatus().name().equals("CONFIRMED") ? 0 : 1) : null)
                .nextCursorEndDate(last != null ? last.getEndDate().toString() : null)
                .nextCursorId(last != null ? last.getId() : null)
                .build();
    }

    @Override
    @Transactional
    public CarReservationDTO cancelReservation(String mid, Long rentalId) {
        CarReservation carReservation = carReservationRepository.findById(rentalId) //차량이 해당 날짜로 예약여부
                .orElseThrow(() -> new RuntimeException("예약을 찾을 수 없습니다."));

        if (!carReservation.getUser().getMid().equals(mid)) {   //본인이 맞는지
            throw new RuntimeException("본인 예약만 취소할 수 있습니다.");
        }

        CarReservationDTO dto = CarReservationDTO.from(carReservation);
        carReservationRepository.delete(carReservation);
        return dto;
    }
}