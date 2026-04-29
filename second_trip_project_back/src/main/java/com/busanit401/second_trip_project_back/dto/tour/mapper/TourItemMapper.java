package com.busanit401.second_trip_project_back.dto.tour.mapper;

import com.busanit401.second_trip_project_back.dto.tour.TourItemResponseDTO;
import com.busanit401.second_trip_project_back.entity.tour.TourItem;
import org.springframework.stereotype.Component;

@Component
public class TourItemMapper {

    // Entity를 DTO로 변환하는 메서드
    public TourItemResponseDTO toDTO(TourItem entity) {

        // 들어온 entity가 null이면 변환할 필요가 없으니 null을 리턴
        if (entity == null) {
            return null;
        }

        // 2. 가격 계산 (기본 가격 + 식사 비용)
        // 만약 null이면 0으로 처리하여 에러 방지
        Long basePrice = entity.getTourPrice() != null ? entity.getTourPrice() : 0L;
        Long mealPrice = entity.getMealPrice() != null ? entity.getMealPrice() : 0L;
        Long totalPrice = basePrice + mealPrice;

        // 3. 빌더 패턴을 사용하여 DTO 생성
        return TourItemResponseDTO.builder()
                .tourId(entity.getTourId())
                .title(entity.getTitle())
                .location(entity.getLocation()) // Entity의 location을 DTO의 tourSpatial로 매핑
                .description(entity.getDescription())
                .subDescription(entity.getDescription()) // 일단 동일하게 넣고, 나중에 다르게 가공해도 됩니다.
                .imageUrl(entity.getImageUrl() != null ? entity.getImageUrl() : "https://default-image-url.com") // null이면 기본 이미지 할당
                .tourPrice(totalPrice)
                .mealPrice(mealPrice)
                .totalPrice(totalPrice)
                .build();
    }
}
