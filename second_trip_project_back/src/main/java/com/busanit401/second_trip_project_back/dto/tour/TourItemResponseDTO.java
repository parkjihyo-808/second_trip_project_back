package com.busanit401.second_trip_project_back.dto.tour;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TourItemResponseDTO {

    //전체조회, 상세조회 모두 사용
    private Long tourId; //고유번호
    private String title; //투어 이름
    private String location; //지역 정보
    private String imageUrl; //썸네일 이미지(구해야됨)
    private Long tourPrice; // 투어 가격(구해야됨)

    //전체조회 시 사용
    private String subDescription; //투어 요약

    //상세조회 시 사용
    private String description; //투어 상세 설명
    private Long mealPrice; // 식당 이용 시 추가금

    //예약 시 사용될 합계 요금
    private Long totalPrice;

}

 /*
 투어 상품 정보에 대한 dto(서비스로부터 불러올 내용들)
 상품 전체 리스트 조회와 상세보기 시 사용

 썸네일 이미지 구해야됨
 가격 표기 방법 찾아야됨
 (해당 투어의 가격을 두고, 식당 이용 시 식사 금액 포함된 가격을 최종금액으로 표기 예정)
 (투어 가격과 식당 가격은 공공데이터에서 구할 수 없음)
 */