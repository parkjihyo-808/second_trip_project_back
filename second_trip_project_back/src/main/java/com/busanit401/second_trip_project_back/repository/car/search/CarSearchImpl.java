package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.domain.car.QCar;
import com.busanit401.second_trip_project_back.domain.car.QRentCarCompany;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPQLQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CarSearchImpl extends QuerydslRepositorySupport implements CarSearch {

    public CarSearchImpl() {
        super(Car.class);
    }

    private static final QCar car = QCar.car;
    private static final QRentCarCompany company = QRentCarCompany.rentCarCompany;

    /**
     * SELECT car.*
     *   FROM car
     *   INNER JOIN rent_car_company company ON car.company_id = company.id
     *   WHERE car.name = :carName
     *     AND company.region = :region
     *     AND car.id NOT IN (:unavailableIds)  -- unavailableIds 있을 때만
     *   ORDER BY car.daily_price ASC, car.id ASC
     *   LIMIT :size OFFSET :page * :size
     *   car테이블에 rent_car_company 테이블을 join 해서 차이름, 지역, 렌트가능여부를 확인해 페이지네이션함
     * @param carName
     * @param region
     * @param unavailableIds
     * @param pageable
     * @return
     */
    @Override
    public List<Car> searchCompanyCarsByPage(String carName, String region, List<Long> unavailableIds, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(car.name.eq(carName))
                .and(company.region.eq(region));
        if (!unavailableIds.isEmpty()) {
            where.and(car.id.notIn(unavailableIds));
        }

        JPQLQuery<Car> query = from(car)
                .join(car.company, company).fetchJoin()
                .where(where)
                .orderBy(car.dailyPrice.asc(), car.id.asc());

        return getQuerydsl().applyPagination(pageable, query).fetch();
    }

    /**
     * SELECT COUNT(car.id)
     *   FROM car
     *   INNER JOIN rent_car_company company ON car.company_id = company.id
     *   WHERE car.name = :carName
     *     AND company.region = :region
     *     AND car.id NOT IN (:unavailableIds)
     * 마지막 and구문은 이미 예약된 차량이 있을때만 사용
     * 객체를 리턴하는게 아닌 갯수만 반환 하는 거라 fetchJoin을 사용하지 않음
     * 차량 테이블에에 회사 id가 같은 company 테이블을 붙이고 차 이름과, 지역이 같고, 빌릴수 있는 차의 갯수를 출력
     * @param carName
     * @param region
     * @param unavailableIds
     * @return
     */
    @Override
    public int searchCountByCarNameAndRegion(String carName, String region, List<Long> unavailableIds) {
        BooleanBuilder where = new BooleanBuilder()
                .and(car.name.eq(carName))
                .and(company.region.eq(region));
        if (!unavailableIds.isEmpty()) {
            where.and(car.id.notIn(unavailableIds));
        }

        return (int) from(car)
                .join(car.company, company)
                .where(where)
                .fetchCount();
    }

    /**
     * SELECT car.name, car.type, car.seats, car.fuel, MIN(car.daily_price)
     *   FROM car
     *   INNER JOIN rent_car_company company ON car.company_id = company.id
     *   WHERE company.region = :region
     *     AND car.id NOT IN (:unavailableIds)  -- unavailableIds 있을 때만
     *   GROUP BY car.name, car.type, car.seats, car.fuel
     *   HAVING MIN(car.daily_price) > :cursorPrice
     *       OR (MIN(car.daily_price) = :cursorPrice AND car.name > :cursorName)
     *   ORDER BY MIN(car.daily_price) ASC, car.name ASC
     *   LIMIT :size OFFSET 0
     * car테이블에 회사 테이블을 조인한 후 지역과 렌트가능여부로 데이터를 읽은후 (차이름, 차타입, 좌석수, 차연료)가 같은 걸로 그룹핑 해서 최저가를 구한후 커서가격과 커서이름으로 페이지네이셚해서 차종을 빼냄
     * @param region
     * @param unavailableIds
     * @param cursorPrice
     * @param cursorName
     * @param pageable
     * @return
     */
    @Override
    public List<CarTypeDTO> searchCarTypesByRegionWithCursor(String region, List<Long> unavailableIds, int cursorPrice, String cursorName, Pageable pageable) {
        BooleanBuilder where = new BooleanBuilder()
                .and(company.region.eq(region));
        if (!unavailableIds.isEmpty()) {
            where.and(car.id.notIn(unavailableIds));
        }

        NumberExpression<Integer> minPrice = car.dailyPrice.min();

        JPQLQuery<CarTypeDTO> query = from(car)
                .join(car.company, company)
                .where(where)
                .groupBy(car.name, car.type, car.seats, car.fuel)
                .having(minPrice.gt(cursorPrice)
                        .or(minPrice.eq(cursorPrice).and(car.name.gt(cursorName))))
                .orderBy(minPrice.asc(), car.name.asc())
                .select(Projections.constructor(CarTypeDTO.class,
                        car.name, car.type, car.seats, car.fuel, minPrice));

        return getQuerydsl().applyPagination(pageable, query).fetch();
    }

    /**
     * SELECT car.*
     *   FROM car
     *   INNER JOIN rent_car_company company ON car.company_id = company.id
     *   WHERE car.name IN (:carNames)
     *     AND company.region = :region
     *     AND car.id NOT IN (:unavailableIds)  -- unavailableIds 있을 때만
     *   ORDER BY car.name ASC, car.daily_price ASC, car.id ASC
     *   car테이블에 회사 테이블을 조인 한 후에 읽어온 차종리스트에서 지역과 차량 렌트 가능 여부로 차량 데이터를 뽑음
     * @param carNames
     * @param region
     * @param unavailableIds
     * @return
     */
    @Override
    public List<Car> searchOptionsByCarNamesIn(List<String> carNames, String region, List<Long> unavailableIds) {
        BooleanBuilder where = new BooleanBuilder()
                .and(car.name.in(carNames))
                .and(company.region.eq(region));
        if (!unavailableIds.isEmpty()) {
            where.and(car.id.notIn(unavailableIds));
        }

        return from(car)
                .join(car.company, company).fetchJoin()
                .where(where)
                .orderBy(car.name.asc(), car.dailyPrice.asc(), car.id.asc())
                .fetch();
    }
}