package com.busanit401.second_trip_project_back.service.tour;

import com.busanit401.second_trip_project_back.dto.tour.TourItemResponseDTO;
import com.busanit401.second_trip_project_back.dto.tour.mapper.TourItemMapper;
import com.busanit401.second_trip_project_back.entity.tour.TourItem;
import com.busanit401.second_trip_project_back.repository.tour.TourItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**투어 아이템 관련 비즈니스 로직을 처리하는 서비스 클래스**/
@Service
@Transactional // 클래스 단위 설정: 메서드 실행 중 예외 발생 시 자동 Rollback(데이터 무결성 보장)
@RequiredArgsConstructor
public class TourItemService {

    private final TourItemRepository tourItemRepository;
    private final TourItemMapper tourItemMapper;


    //전체 상품 조회
    @Transactional(readOnly = true) // 조회 전용: DB 성능 최적화 및 읽기 전용 작업 설정
    public Page<TourItemResponseDTO> getList(Pageable pageable) {
        //DB에서 엔티티 리스트를 페이징 처리하여 가져옴
        Page<TourItem> tourPage = tourItemRepository.findAll(pageable);
        //map함수: 배열을 순회하며 DTO로 변환함
        return tourPage.map(tourItemMapper::toDTO);
    }

    //상세 조회(하나 조회)
    @Transactional(readOnly = true)
    public TourItemResponseDTO getOne(Long tourId) {
        // ID로 데이터를 찾고, 없으면 예외를 발생시켜 비정상적인 접근 차단
        TourItem entity = tourItemRepository.findById(tourId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 투어 아이템입니다."));

        // 엔티티를 DTO로 변환하여 외부로 안전하게 반환
        return tourItemMapper.toDTO(entity);
    }
}