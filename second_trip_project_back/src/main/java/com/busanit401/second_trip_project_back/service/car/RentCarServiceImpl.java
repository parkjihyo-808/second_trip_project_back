package com.busanit401.second_trip_project_back.service.car;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.dto.car.CarSearchCursorResponseDTO;
import com.busanit401.second_trip_project_back.dto.car.CarSearchResultDTO;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import com.busanit401.second_trip_project_back.dto.car.CompanyCarPageResponseDTO;
import com.busanit401.second_trip_project_back.repository.car.CarRepository;
import com.busanit401.second_trip_project_back.repository.car.RentCarCompanyRepository;
import com.busanit401.second_trip_project_back.repository.car.CarReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    // 특정 차량의 업체별 제원 - 페이지 기반
    @Override
    @Transactional(readOnly = true) //여러 쿼리로 순서대로 쓰기 때문에 중간에 변경이 있을때를 대비해서
    public CompanyCarPageResponseDTO searchCompanyCars(String carName, String region, LocalDate startDate, LocalDate endDate, int page, int size) {
        List<Long> unavailableIds = carReservationRepository.searchUnavailableCarIds(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)   //지역 시간 최대로
        );

        int totalCount = carRepository.searchCountByCarNameAndRegion(carName, region, unavailableIds);  //해당 지역과 차이름, 렌트 가능 여부로 차량의 갯수을 가져옴

        List<Car> cars = carRepository.searchCompanyCarsByPage(carName, region, unavailableIds, PageRequest.of(page, size));    //페이지 네이션 한 데이터

        List<CarSearchResultDTO.CompanyCarDTO> companyCarDTOs = cars.stream()
                .map(car -> CarSearchResultDTO.CompanyCarDTO.builder()
                        .carId(car.getId())
                        .companyName(car.getCompany().getName())
                        .year(car.getYear())
                        .dailyPrice(car.getDailyPrice())
                        .build())
                .toList();

        int totalPages = (int) Math.ceil((double) totalCount / size);   //총 페이지 계산

        return CompanyCarPageResponseDTO.builder()
                .companyCarDTOs(companyCarDTOs)
                .page(page)
                .size(size)
                .totalCount(totalCount)
                .totalPages(totalPages)
                .hasNext(page + 1 < totalPages)
                .build();
    }

    // 차종(커서) + 제원(페이지) 조합 - 쿼리 2번
    @Override
    @Transactional(readOnly = true)
    public CarSearchCursorResponseDTO searchCarsWithCompanyCars(String region, LocalDate startDate, LocalDate endDate, int cursorPrice, String cursorName, int size, int companyCarSize) {  //커서 두개로 하는 이유는 가격이 겹칠때를 대비해서 이름까지 넣어줌
        List<Long> unavailableIds = carReservationRepository.searchUnavailableCarIds(
                startDate.atStartOfDay(),
                endDate.atTime(LocalTime.MAX)
        );

        //carTypes - 차옵션(이름, 좌석수, 연료, 타입)으로 그룹핑한 데이터에 차량 페이지 네이션으로 가져옴(GROUP BY로 집계한 데이터라 개별 차량/업체 정보가 없음)
        //carNames - carTypes에서 차이름을 리스트로 저장
        //companyCars - carNames로 지역에서 빌릴수 있는 개별 차량의 데이터를 가져옴
        //companyCarMap - companyCars로 차량이름을 key로 쓰는 차량 데이터를 만듬(차종별로 매칭하려고)

        // 쿼리 1 - 커서 기반으로 차종 가져옴(해당지역에서 빌릴수 있는 차종들)
        Pageable pageable = PageRequest.of(0, size);
        List<CarTypeDTO> carTypes = carRepository.searchCarTypesByRegionWithCursor(region, unavailableIds, cursorPrice, cursorName, pageable);  //(차이름, 차타입, 좌석수, 차연료)로 그룹핑된 페이지네이션한 데이터

        if (carTypes.isEmpty()) {
            return CarSearchCursorResponseDTO.builder()
                    .content(List.of())
                    .hasNext(false)
                    .build();
        }

        // 쿼리 2 - 현재 차종들 IN절로 옵션 한 번에 조회
        List<String> carNames = carTypes.stream().map(CarTypeDTO::getCarName).toList(); //읽어온 데이터에서 차이름만 리스트로 뽑아냄
        List<Car> companyCars = carRepository.searchOptionsByCarNamesIn(carNames, region, unavailableIds);  //차이름이 포함된 해당지역의 빌릴수 있는 차 데이터를 뽑음

        Map<String, List<Car>> companyCarMap = companyCars.stream()
                .collect(Collectors.groupingBy(Car::getName));  //차량 이름으로 차데이터를 group지어서 맵으로 저장

        List<CarSearchResultDTO> content = carTypes.stream()
                .map(ct -> {
                    //페이지네이션으로 읽어온 차량 데이터 1개씩 스트림 하는데
                    List<Car> group = companyCarMap.getOrDefault(ct.getCarName(), List.of());   //companyCarMap에 carType의 차이름과 같은게 있다면 리스트에 값을 넣고 없으면 null
                    int totalCompanyCarCount = group.size();    //실제 회사차량의 총 갯수
                    boolean hasNextOption = totalCompanyCarCount > companyCarSize;  //앱에서 주는 companyCarSize는 3이라서 3개보다 많으면 다음이 있음
                    List<Car> sliced = hasNextOption ? group.subList(0, companyCarSize) : group;    //최대 3개만 잘라서 차량 데이터 저장

                    List<CarSearchResultDTO.CompanyCarDTO> companyCarDTOs = sliced.stream()
                            .map(c -> CarSearchResultDTO.CompanyCarDTO.builder()
                                    .carId(c.getId())
                                    .companyName(c.getCompany().getName())
                                    .year(c.getYear())
                                    .dailyPrice(c.getDailyPrice())
                                    .build())
                            .toList();

                    Car lastCar = hasNextOption ? sliced.get(sliced.size() - 1) : null;
                    return CarSearchResultDTO.builder()
                            .carName(ct.getCarName())
                            .type(ct.getType())
                            .seats(ct.getSeats())
                            .fuel(ct.getFuel())
                            .lowestPrice(ct.getLowestPrice())
                            .companyCarDTOs(companyCarDTOs)
                            .totalCompanyCarCount(totalCompanyCarCount)
                            .nextCursorPrice(lastCar != null ? lastCar.getDailyPrice() : null)
                            .nextCursorId(lastCar != null ? lastCar.getId() : null)
                            .build();
                })
                .toList();  //CarSearchResultDTO형식으로 데이터를 채워서 리스트로 만들어줌


        CarTypeDTO lastType = carTypes.get(carTypes.size() - 1);    //커서로 지정할 값을 가져오기 위해 마지막으로 읽어온 타입을 넣어줌
        return CarSearchCursorResponseDTO.builder()
                .content(content)
                .hasNext(carTypes.size() == size)
                .nextCursorPrice(lastType.getLowestPrice())
                .nextCursorName(lastType.getCarName())
                .build();
        //CarSearchCursorResponseDTO형태로 반환
    }
}