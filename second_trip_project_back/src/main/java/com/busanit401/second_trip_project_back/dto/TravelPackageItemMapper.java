package com.busanit401.second_trip_project_back.dto;

import com.busanit401.second_trip_project_back.domain.packages.TravelPackageItem;
import org.springframework.stereotype.Component;

@Component
public class TravelPackageItemMapper {

    public TravelPackageItemDTO toDTO(TravelPackageItem entity) {
        // [방어 로직] 엔티티가 null이면 매핑을 중단하고 null 반환
        if (entity == null) {
            return null;
        }

        // [빌더를 이용한 매핑]
        // 엔티티의 값을 DTO의 필드에 하나씩 꼼꼼하게 넣어줍니다.
        return TravelPackageItemDTO.builder()
                .id(entity.getId())
                .category(entity.getCategory() != null ? entity.getCategory() : "기타")
                .title(entity.getTitle() != null ? entity.getTitle() : "제목 없음")
                .description(entity.getDescription())
                .region(entity.getRegion() != null ? entity.getRegion() : "전국")
                .thumbnail(entity.getThumbnail() != null ? entity.getThumbnail() : "https://default-image.com")
                .price(entity.getPrice())
                .tags(entity.getTags())
                .minPeople(entity.getMinPeople() != null ? entity.getMinPeople() : 1)
                .maxPeople(entity.getMaxPeople() != null ? entity.getMaxPeople() : 10)

                // 상세 정보들
                .inclusions(entity.getInclusions())
                .exclusions(entity.getExclusions())
                .flightInfo(entity.getFlightInfo())
                .itinerary(entity.getItinerary())
                .build();
    }
}
