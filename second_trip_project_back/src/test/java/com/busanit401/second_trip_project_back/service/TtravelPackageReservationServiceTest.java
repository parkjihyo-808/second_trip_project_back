package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.packages.TravelPackageItem;
import com.busanit401.second_trip_project_back.dto.TravelPackageItemDTO;
import com.busanit401.second_trip_project_back.dto.TravelPackageItemMapper;
import com.busanit401.second_trip_project_back.repository.TravelPackageItemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // Mockito 사용 선언
class TravelPackageServiceTest {

    @Mock
    private TravelPackageItemRepository repository;

    @Mock
    private TravelPackageItemMapper mapper;

    @InjectMocks
    private TravelPackageService service; // Mock 객체들이 이 서비스에 주입됨

    @Test
    @DisplayName("패키지 상세 조회 성공 테스트")
    void getOne_Success() {
        // [Given: 환경 설정]
        Long id = 1L;
        TravelPackageItem entity = TravelPackageItem.builder().id(id).title("테스트 패키지").build();
        TravelPackageItemDTO dto = TravelPackageItemDTO.builder().id(id).title("테스트 패키지").build();

        // Repository와 Mapper가 어떻게 동작할지 미리 지정 (Stubbing)
        when(repository.findById(id)).thenReturn(Optional.of(entity));
        when(mapper.toDTO(entity)).thenReturn(dto);

        // [When: 테스트 실행]
        TravelPackageItemDTO result = service.getOne(id);

        // [Then: 검증]
        assertThat(result.getTitle()).isEqualTo("테스트 패키지");
        verify(repository, times(1)).findById(id); // DB 조회가 딱 한 번 호출되었는지 확인
    }

    @Test
    @DisplayName("패키지 상세 조회 실패 시 예외 발생 테스트")
    void getOne_NotFound() {
        // [Given]
        Long id = 999L;
        when(repository.findById(id)).thenReturn(Optional.empty());

        // [Then & When]
        assertThrows(IllegalArgumentException.class, () -> service.getOne(id));
    }
}