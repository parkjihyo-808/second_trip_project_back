package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.dto.tour.TourItemResponseDTO;
import com.busanit401.second_trip_project_back.dto.tour.mapper.TourItemMapper;
import com.busanit401.second_trip_project_back.entity.tour.TourItem;
import com.busanit401.second_trip_project_back.repository.tour.TourItemRepository;
import com.busanit401.second_trip_project_back.service.tour.TourItemService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// JUnit 5와 Mockito를 연결하여 테스트 환경을 구성합니다.
@ExtendWith(MockitoExtension.class)
class TourItemServiceTest {

    // 1. @Mock: 가짜 Repository를 만듭니다. (진짜 DB에 연결하지 않음)
    @Mock
    private TourItemRepository tourItemRepository;

    @Mock
    private TourItemMapper tourItemMapper;

    // 2. @InjectMocks: 위에서 만든 가짜 Repository를 '투입'할 서비스 객체입니다.
    // 서비스가 DB 없이도 돌아갈 수 있게 준비된 환경입니다.
    @InjectMocks
    private TourItemService tourItemService;

    @Test
    @DisplayName("투어 아이템 하나 조회 성공 테스트")
    void getOneSuccess() {
        // [준비]
        Long tourId = 1L;
        TourItem mockItem = TourItem.builder().tourId(tourId).title("해운대 투어").build();

        // 테스트용 DTO 생성 (실제 서비스가 반환할 형태)
        TourItemResponseDTO mockDto = TourItemResponseDTO.builder()
                .title("해운대 투어")
                .build();

        // 1. Repository 동작 설정 (엔티티 반환)
        when(tourItemRepository.findById(tourId)).thenReturn(Optional.of(mockItem));

        // 2. Mapper 동작 설정 (중요!)
        // "Service가 mapper.toDTO(mockItem)을 호출하면 mockDto를 돌려줘!"
        when(tourItemMapper.toDTO(mockItem)).thenReturn(mockDto);

        // [실행]
        TourItemResponseDTO result = tourItemService.getOne(tourId); // 타입 변경

        // [검증]
        assertThat(result.getTitle()).isEqualTo("해운대 투어");

        // 추가 검증: 서비스가 레포지토리와 매퍼를 정확히 호출했는지 확인
        verify(tourItemRepository).findById(tourId);
        verify(tourItemMapper).toDTO(mockItem);
    }

    @Test
    @DisplayName("투어 아이템 하나 조회 실패 테스트 (데이터 없음)")
    void getOneFail() {
        // [준비] "만약 id 99번을 찾으면, 아무것도 없다고(empty) 해라!"
        Long tourId = 99L;
        when(tourItemRepository.findById(tourId)).thenReturn(Optional.empty());

        // [실행 및 검증] 없는 데이터를 조회할 때 우리가 설정한 예외(IllegalArgumentException)가 발생하는지 확인
        assertThrows(IllegalArgumentException.class, () -> tourItemService.getOne(tourId));
    }

    @Test
    @DisplayName("투어 아이템 전체 조회 테스트")
    void getListTest() {
        // [준비] 가짜 데이터 2개 생성
        TourItem item1 = TourItem.builder().title("투어1").build();
        TourItem item2 = TourItem.builder().title("투어2").build();
        List<TourItem> list = Arrays.asList(item1, item2);

        // [준비] 페이징 처리 설정 (0페이지에 10개씩)
        Pageable pageable = PageRequest.of(0, 10);
        // 가짜 페이지 객체 생성 (실제 데이터와 페이징 정보를 합침)
        Page<TourItem> mockPage = new PageImpl<>(list, pageable, list.size());

        // 3. (중요) 가짜 Mapper가 어떻게 동작해야 할지 알려주기 (Stubbing)
        // entity가 들어오면, DTO를 리턴하도록 약속합니다.
        when(tourItemMapper.toDTO(any(TourItem.class))).thenReturn(new TourItemResponseDTO());
        when(tourItemRepository.findAll(pageable)).thenReturn(mockPage);

        // when: 서비스 로직 실행
        Page<TourItemResponseDTO> result = tourItemService.getList(pageable);

        // then: 검증
        assertThat(result).isNotNull();
        verify(tourItemRepository).findAll(pageable); // Repository가 호출되었는지 확인
    }
}

/*가짜 레포지토리 생성하여 서비스 함수 테스트 완료
* 서비스의 getOne 호출
* 서비스의 getList 호출*/