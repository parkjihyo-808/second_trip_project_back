package com.busanit401.second_trip_project_back.config;

import com.busanit401.second_trip_project_back.entity.tour.TourItem;
import com.busanit401.second_trip_project_back.repository.tour.TourItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner { // CommandLineRunner: 스프링 부트가 시작된 직후, run() 메서드를 자동으로 실행하게 해주는 인터페이스입니다.

    private final TourItemRepository tourItemRepository;

    @Override
    public void run(String... args) throws Exception { // 애플리케이션 가동 완료 시점(시점: 서버 준비 끝!)에 동작합니다.

        // 5. 테스트용 데이터를 생성합니다. (더미 데이터)
        TourItem item1 = TourItem.builder()
                .title("test_부산 야경 투어")
                .location("test_부산")
                .description("test_아름다운 부산의 야경을 즐기는 투어입니다.")
                .tourPrice(500L)
                .mealPrice(500L)
                .build();

        TourItem item2 = TourItem.builder()
                .title("test_해운대 요트 투어")
                .location("test_해운대")
                .description("test_바다 위에서 즐기는 힐링 요트 체험.")
                .tourPrice(500L)
                .mealPrice(500L)
                .imageUrl("https://placehold.co/600x400?text=Yacht+Tour")
                .build();

        // 6. 생성한 데이터를 DB에 저장합니다.
        tourItemRepository.save(item1);
        tourItemRepository.save(item2);

        System.out.println("테스트 데이터 2건 생성 완료!");
    }
}

/*초기 데이터 생성을 위한 임시 파일
* 서버 시작 시 비어있는 DB값에 들어갈 테스트용 데이터*/