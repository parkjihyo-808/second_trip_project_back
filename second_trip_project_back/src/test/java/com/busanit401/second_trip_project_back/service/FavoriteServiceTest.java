package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.member.MemberRole;
import com.busanit401.second_trip_project_back.dto.loging.FavoriteRequestDTO;
import com.busanit401.second_trip_project_back.dto.loging.FavoriteResponseDTO;
import com.busanit401.second_trip_project_back.entity.Favorite;
import com.busanit401.second_trip_project_back.repository.FavoriteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("찜 서비스 테스트")
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private Member testMember;
    private FavoriteRequestDTO requestDTO;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        // 테스트용 회원 생성
        testMember = Member.builder()
                .id(1L)
                .mid("test@test.com")
                .mpw("1234")
                .mname("테스터")
                .email("test@test.com")
                .role(MemberRole.USER)
                .build();

        // 테스트용 찜 요청 DTO
        requestDTO = FavoriteRequestDTO.builder()
                .contentId("126508")
                .accommodationTitle("해운대 그랜드 호텔")
                .firstImage("http://image.url/test.jpg")
                .addr1("부산광역시 해운대구")
                .build();

        // 테스트용 찜 엔티티
        testFavorite = Favorite.builder()
                .id(1L)
                .member(testMember)
                .contentId("126508")
                .accommodationTitle("해운대 그랜드 호텔")
                .firstImage("http://image.url/test.jpg")
                .addr1("부산광역시 해운대구")
                .build();
    }

    @Test
    @DisplayName("찜 추가 성공")
    void addFavorite_success() {
        // given
        // 아직 찜 안 한 상태
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(false);

        given(favoriteRepository.save(any()))
                .willReturn(testFavorite);

        // when
        FavoriteResponseDTO response =
                favoriteService.addFavorite(testMember, requestDTO);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getContentId()).isEqualTo("126508");
        assertThat(response.getAccommodationTitle())
                .isEqualTo("해운대 그랜드 호텔");
        verify(favoriteRepository).save(any());
    }

    @Test
    @DisplayName("이미 찜한 숙소 중복 추가 실패")
    void addFavorite_fail_duplicate() {
        // given
        // 이미 찜한 상태
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(true);

        // when & then
        assertThatThrownBy(() ->
                favoriteService.addFavorite(testMember, requestDTO))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 찜한 숙소입니다.");
    }

    @Test
    @DisplayName("찜 삭제 성공")
    void removeFavorite_success() {
        // given
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(true);

        // when
        favoriteService.removeFavorite(testMember, "126508");

        // then
        verify(favoriteRepository)
                .deleteByMemberAndContentId(testMember, "126508");
    }

    @Test
    @DisplayName("찜 안한 숙소 삭제 실패")
    void removeFavorite_fail_notFound() {
        // given
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(false);

        // when & then
        assertThatThrownBy(() ->
                favoriteService.removeFavorite(testMember, "126508"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("찜한 숙소를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 찜 목록 조회 성공")
    void getMyFavorites_success() {
        // given
        given(favoriteRepository.findByMemberOrderByRegDateDesc(testMember))
                .willReturn(List.of(testFavorite));

        // when
        List<FavoriteResponseDTO> response =
                favoriteService.getMyFavorites(testMember);

        // then
        assertThat(response).isNotNull();
        assertThat(response).hasSize(1);
        assertThat(response.get(0).getContentId()).isEqualTo("126508");
    }

    @Test
    @DisplayName("찜 여부 확인 - 찜한 경우")
    void isFavorite_true() {
        // given
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(true);

        // when
        boolean result =
                favoriteService.isFavorite(testMember, "126508");

        // then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("찜 여부 확인 - 찜 안한 경우")
    void isFavorite_false() {
        // given
        given(favoriteRepository.existsByMemberAndContentId(
                testMember, "126508"))
                .willReturn(false);

        // when
        boolean result =
                favoriteService.isFavorite(testMember, "126508");

        // then
        assertThat(result).isFalse();
    }
}