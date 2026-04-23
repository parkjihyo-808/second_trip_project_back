package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.Car;
import com.busanit401.second_trip_project_back.domain.car.QCar;
import com.busanit401.second_trip_project_back.domain.car.QRentCarCompany;
import com.busanit401.second_trip_project_back.dto.car.CarTypeDTO;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class CarSearchImpl extends QuerydslRepositorySupport implements CarSearch {

    public CarSearchImpl() {
        super(Car.class);
    }

    private static final QCar car = QCar.car;
    private static final QRentCarCompany company = QRentCarCompany.rentCarCompany;

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
     *   LIMIT :limit
     * car테이블에 회사 테이블을 조인한 후 지역과 렌트가능여부로 데이터를 읽은후 (차이름, 차타입, 좌석수, 차연료)가 같은 걸로 그룹핑 해서 최저가를 구한후 커서가격과 커서이름으로 페이지네이션해서 차종을 빼냄
     * @param region
     * @param unavailableIds
     * @param cursorPrice
     * @param cursorName
     * @param limit
     * @return
     */
    @Override
    public List<CarTypeDTO> searchCarTypesByRegionWithCursor(String region, List<Long> unavailableIds, int cursorPrice, String cursorName, int limit) {
        BooleanBuilder where = new BooleanBuilder()
                .and(company.region.eq(region));
        if (!unavailableIds.isEmpty()) {
            where.and(car.id.notIn(unavailableIds));
        }

        NumberExpression<Integer> minPrice = car.dailyPrice.min();

        return from(car)
                .join(car.company, company)
                .where(where)
                .groupBy(car.name, car.type, car.seats, car.fuel)
                .having(minPrice.gt(cursorPrice)
                        .or(minPrice.eq(cursorPrice).and(car.name.gt(cursorName))))
                .orderBy(minPrice.asc(), car.name.asc())
                .select(Projections.constructor(CarTypeDTO.class,
                        car.name, car.type, car.seats, car.fuel, minPrice))
                .limit(limit)
                .fetch();
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