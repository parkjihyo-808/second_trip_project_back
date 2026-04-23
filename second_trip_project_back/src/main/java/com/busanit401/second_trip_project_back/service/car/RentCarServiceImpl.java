package com.busanit401.second_trip_project_back.service.car;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchResultDTO;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import com.busanit401.second_trip_project_back.repository.car.CarRepository;
import com.busanit401.second_trip_project_back.repository.car.RentCarCompanyRepository;
import com.busanit401.second_trip_project_back.repository.car.CarReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Log4j2
@RequiredArgsConstructor
public class RentCarServiceImpl implements RentCarService {

    private final RentCarCompanyRepository rentCarCompanyRepository;
    private final CarReservationRepository carReservationRepository;
    private final CarRepository carRepository;

    @Override
    public List<String> getRegions() {
        return rentCarCompanyRepository.searchDistinctRegions();
    }

    // 차종(커서) + 업체 차량 전체 조합 - 쿼리 2번
    @Override
    @Transactional(readOnly = true)
    public CarSearchCursorResponseDTO searchCarsWithCompanyCars(
            String region,
            LocalDate startDate,
            LocalDate endDate,
            int cursorPrice,
            String cursorName,
            int size
    ) {  //커서 두개로 하는 이유는 가격이 겹칠때를 대비해서 이름까지 넣어줌
        List<Long> unavailableIds = carReservationRepository.searchUnavailableCarIds(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        //carTypes - 차옵션(이름, 좌석수, 연료, 타입)으로 그룹핑한 데이터에 차량 페이지 네이션으로 가져옴(GROUP BY로 집계한 데이터라 개별 차량/업체 정보가 없음)
        //carNames - carTypes에서 차이름을 리스트로 저장
        //companyCars - carNames로 지역에서 빌릴수 있는 개별 차량의 데이터를 가져옴
        //companyCarMap - companyCars로 차량이름을 key로 쓰는 차량 데이터를 만듬(차종별로 매칭하려고)

        // 쿼리 1 - 커서 기반으로 차종 가져옴(해당지역에서 빌릴수 있는 차종들)
        // size+1개를 가져와서 다음 페이지 존재 여부 판단 (size의 배수일 때 hasNext가 잘못 true가 되는 것 방지)
        List<CarTypeDTO> fetched = carRepository.searchCarTypesByRegionWithCursor(
                region,
                unavailableIds,
                cursorPrice,
                cursorName,
                size + 1
        );  //(차이름, 차타입, 좌석수, 차연료)로 그룹핑된 페이지네이션한 데이터

        if (fetched.isEmpty()) {
            return CarSearchCursorResponseDTO.builder()
                    .content(List.of())
                    .hasNext(false)
                    .build();
        }

        boolean hasNext = fetched.size() > size;
        List<CarTypeDTO> carTypes = hasNext ? fetched.subList(0, size) : fetched;   //실제로 반환할 size개

        // 쿼리 2 - 현재 차종들 IN절로 옵션 한 번에 조회
        List<String> carNames = carTypes.stream().map(CarTypeDTO::getCarName).toList(); //읽어온 데이터에서 차이름만 리스트로 뽑아냄
        List<Car> companyCars = carRepository.searchOptionsByCarNamesIn(carNames, region, unavailableIds);  //차이름이 포함된 해당지역의 빌릴수 있는 차 데이터를 뽑음

        Map<String, List<Car>> companyCarMap = companyCars.stream()
                .collect(Collectors.groupingBy(Car::getName));  //차량 이름으로 차데이터를 group지어서 맵으로 저장

        List<CarSearchResultDTO> content = carTypes.stream()
                .map(ct -> {
                    List<Car> group = companyCarMap.get(ct.getCarName());   //차종에 해당하는 업체 차량 전체

                    List<CarSearchResultDTO.CompanyCarDTO> companyCarDTOs = group.stream()
                            .map(c -> CarSearchResultDTO.CompanyCarDTO.builder()
                                    .carId(c.getId())
                                    .companyName(c.getCompany().getName())
                                    .year(c.getYear())
                                    .dailyPrice(c.getDailyPrice())
                                    .build())
                            .toList();

                    return CarSearchResultDTO.builder()
                            .carName(ct.getCarName())
                            .type(ct.getType())
                            .seats(ct.getSeats())
                            .fuel(ct.getFuel())
                            .lowestPrice(ct.getLowestPrice())
                            .companyCarDTOs(companyCarDTOs)
                            .build();
                })
                .toList();

        CarTypeDTO lastType = carTypes.get(carTypes.size() - 1);    //커서로 지정할 값을 가져오기 위해 마지막으로 읽어온 타입을 넣어줌
        return CarSearchCursorResponseDTO.builder()
                .content(content)
                .hasNext(hasNext)
                .nextCursorPrice(lastType.getLowestPrice())
                .nextCursorName(lastType.getCarName())
                .build();
    }
}