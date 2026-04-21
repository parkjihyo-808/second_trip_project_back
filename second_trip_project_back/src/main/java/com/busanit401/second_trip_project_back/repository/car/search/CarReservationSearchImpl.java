package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.CarReservation;
import com.busanit401.second_trip_project_back.domain.car.QCar;
import com.busanit401.second_trip_project_back.domain.car.QCarReservation;
import com.busanit401.second_trip_project_back.domain.member.QMember;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.time.LocalDateTime;
import java.util.List;

public class CarReservationSearchImpl extends QuerydslRepositorySupport implements CarReservationSearch {

    public CarReservationSearchImpl() {
        super(CarReservation.class);
    }

    private static final QCarReservation reservation = QCarReservation.carReservation;
    private static final QCar car = QCar.car;
    private static final QMember member = QMember.member;

    /**
     * SELECT reservation.*, car.*, member.*
     *   FROM car_reservation reservation
     *   INNER JOIN car ON reservation.car_id = car.id
     *   INNER JOIN member ON reservation.user_id = member.id
     *   WHERE reservation.user_mid = :mid
     *     AND (
     *       reservation.end_date < :cursorEndDate
     *       OR (reservation.end_date = :cursorEndDate
     *           AND CASE WHEN reservation.status = 'CONFIRMED' THEN 0 ELSE 1 END > :cursorStatusOrder)
     *       OR (reservation.end_date = :cursorEndDate
     *           AND CASE WHEN reservation.status = 'CONFIRMED' THEN 0 ELSE 1 END = :cursorStatusOrder
     *           AND reservation.id > :cursorId)
     *     )
     *   ORDER BY reservation.end_date DESC,
     *            CASE WHEN reservation.status = 'CONFIRMED' THEN 0 ELSE 1 END ASC,
     *            reservation.id ASC
     *   LIMIT :size
     *   예약 테이블에 차테이블과 유저테이블을 join후 mid가 같으면 읽어옴
     * @param mid
     * @param cursorStatusOrder
     * @param cursorEndDate
     * @param cursorId
     * @param size
     * @return
     */
    @Override
    public List<CarReservation> searchByUserMidWithCursor(String mid, int cursorStatusOrder, LocalDateTime cursorEndDate, Long cursorId, int size) {
        NumberExpression<Integer> statusOrder = new CaseBuilder()
                .when(reservation.status.eq(CarReservation.RentalStatus.CONFIRMED)).then(0)
                .otherwise(1);  //confirm만 있어서 값은 무조건 0

        return from(reservation)
                .join(reservation.car, car).fetchJoin()
                .join(reservation.user, member).fetchJoin()
                .where(reservation.user.mid.eq(mid)
                        /*
                        endDate=5월10일, statusOrder=0, id=7이라면:

                        - endDate < 5월10일 → 날짜가 더 작은 것들 (DESC라 뒤에 오는 것들)
                        - endDate = 5월10일 AND statusOrder > 0 → 날짜 같고 statusOrder 큰 것
                        - endDate = 5월10일 AND statusOrder = 0 AND id > 7 → 다 같고 id만 큰 것
                        이렇게 or로 하면 정렬 순서때문에 커서로 지정할수 있다.
                        즉,정렬 순서 그대로 우선순위 높은 것부터 OR로 쌓아가면 커서를 등록할수 있다.

                         */
                        .and(reservation.endDate.lt(cursorEndDate)  //끝날짜 이전이라면 다음이 있으니깐 커서 지정
                                .or(reservation.endDate.eq(cursorEndDate).and(statusOrder.gt(cursorStatusOrder)))   //끝날짜가 같다면 상태가 컨펌 아닌 것을 커서 지정
                                .or(reservation.endDate.eq(cursorEndDate).and(statusOrder.eq(cursorStatusOrder)).and(reservation.id.gt(cursorId)))))    //끝날짜, 상태가 같다면 예약 id가 커서보다 크다면
                .orderBy(reservation.endDate.desc(), statusOrder.asc(), reservation.id.asc())   //정렬을 이렇게 하기 때문에 커서이후에 모
                .limit(size)    //10개
                .fetch();
    }

    /**
     * 해당 차량의 예약 여부
     * @param carId
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public boolean searchExistsOverlap(Long carId, LocalDateTime startDate, LocalDateTime endDate) {
        return from(reservation)
                .where(reservation.car.id.eq(carId)
                        .and(reservation.startDate.lt(endDate))
                        .and(reservation.endDate.gt(startDate)))
                .fetchFirst() != null;
    }

    /**
     * 해당 아이디로 해당 날짜에 차량 상관없이 예약이 있는지
     * @param mid
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public boolean searchExistsUserOverlap(String mid, LocalDateTime startDate, LocalDateTime endDate) {
        return from(reservation)
                .where(reservation.user.mid.eq(mid)
                        .and(reservation.startDate.lt(endDate))
                        .and(reservation.endDate.gt(startDate)))
                .fetchFirst() != null;
    }

    /**
     * SELECT DISTINCT c.id
     *   FROM car_reservation r
     *   INNER JOIN car c ON r.car_id = c.id
     *   WHERE r.start_date < :endDate AND r.end_date > :startDate
     *   예약 테이블의 예약 시작과, 예약 끝 시간 사이의 차량의 id와 차량 테이블의 id가 겹치는 걸 join해서 차량 id를 select함
     *   빌릴수 없는 차량 리스트(해당시간에 예약이 있는지 보는것)
     * @param startDate
     * @param endDate
     * @return
     */
    @Override
    public List<Long> searchUnavailableCarIds(LocalDateTime startDate, LocalDateTime endDate) {
        return from(reservation)
                .join(reservation.car, car)
                .where(reservation.startDate.lt(endDate)
                        .and(reservation.endDate.gt(startDate)))
                .select(car.id)
                .distinct() //겹칠수는 없게 앱에서 프로그램을 작성 했지만 혹시 모를 버그를 대비
                .fetch();
    }
}