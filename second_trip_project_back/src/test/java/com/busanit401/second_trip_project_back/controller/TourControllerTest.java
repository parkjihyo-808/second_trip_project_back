package com.busanit401.second_trip_project_back.controller;

import com.busanit401.second_trip_project_back.controller.tour.TourController;
import com.busanit401.second_trip_project_back.dto.tour.TourItemResponseDTO;
import com.busanit401.second_trip_project_back.service.tour.TourItemService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class) // Spring을 띄우지 않고 Mockito만 사용하겠다는 선언
class TourControllerTest {

    @Mock
    private TourItemService tourItemService; // 서비스는 가짜로!

    @InjectMocks
    private TourController tourController; // 컨트롤러에 가짜 서비스를 자동으로 넣어줌

    @Test
    void getOne_Test() {
        // [준비]
        Long tourId = 1L;
        TourItemResponseDTO mockDto = TourItemResponseDTO.builder().title("해운대 투어").build();

        when(tourItemService.getOne(tourId)).thenReturn(mockDto);

        // [실행]
        TourItemResponseDTO result = tourController.getOne(tourId);

        // [검증]
        assertThat(result.getTitle()).isEqualTo("해운대 투어");
        verify(tourItemService).getOne(tourId); // 서비스가 호출되었는지 확인
    }
}