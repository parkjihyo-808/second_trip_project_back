package com.busanit401.second_trip_project_back.service;


import com.busanit401.second_trip_project_back.domain.packages.TravelPackageItem;
import com.busanit401.second_trip_project_back.dto.TravelPackageItemDTO;
import com.busanit401.second_trip_project_back.dto.TravelPackageItemMapper;
import com.busanit401.second_trip_project_back.repository.TravelPackageItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**패키지 아이템 관련 비즈니스 로직 서비스**/
@Service
@Transactional
@RequiredArgsConstructor
public class TravelPackageService {

    private final TravelPackageItemRepository travelPackageItemRepository;
    private final TravelPackageItemMapper travelPackageItemMapper;

    // 전체 리스트 조회
    @Transactional(readOnly = true)
    public Page<TravelPackageItemDTO> getList(Pageable pageable) {
        // 1. DB에서 엔티티를 페이징 처리하여 가져옴
        // 2. map함수로 순회하며 가져온 데이터를 packageItemPage에 넣음
        Page<TravelPackageItem> packageItemPage = travelPackageItemRepository.findAll(pageable);
        return packageItemPage.map(travelPackageItemMapper::toDTO);
    }

    //하나 조회(상세보기)
    @Transactional(readOnly = true)
    public TravelPackageItemDTO getOne(Long id) {
        // 1. DB에서 ID로 찾기 (없으면 바로 Exception 발생)
        // 2. Mapper를 통해 DTO로 변환하여 리턴
        TravelPackageItem entity = travelPackageItemRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 패키지를 찾을 수 없습니다. ID: " + id));

        return travelPackageItemMapper.toDTO(entity);
    }
}