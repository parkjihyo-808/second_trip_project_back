package com.busanit401.second_trip_project_back.repository.car.search;

import com.busanit401.second_trip_project_back.domain.car.QRentCarCompany;
import com.busanit401.second_trip_project_back.domain.car.RentCarCompany;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;

import java.util.List;

public class RentCarCompanySearchImpl extends QuerydslRepositorySupport implements RentCarCompanySearch {

    public RentCarCompanySearchImpl() {
        super(RentCarCompany.class);
    }

    private static final QRentCarCompany company = QRentCarCompany.rentCarCompany;

    /**
     * 지역 중복 제거후 지역 데이터 가져옴(렌트카 검색할 지역선택시 사용)
     * @return
     */
    @Override
    public List<String> searchDistinctRegions() {
        return from(company)
                .select(company.region)
                .distinct()
                .orderBy(company.region.asc())  //지역 글자 순으로 정렬
                .fetch();
    }
}