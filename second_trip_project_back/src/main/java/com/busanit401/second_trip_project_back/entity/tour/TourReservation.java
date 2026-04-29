package com.busanit401.second_trip_project_back.entity.tour;

import com.busanit401.second_trip_project_back.domain.member.Member; // Member 패키지 경로에 맞게 수정해주세요
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "trip_tour_reservation")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TourReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    //고유 식별자(PK)
    private Long reservationId;

    /** 예약자 정보 (Member 엔티티와 N:1 관계) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    /** 예약한 투어 정보 (TourItem 엔티티와 N:1 관계) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tour_id")
    private TourItem tourItem;

    //방문 예정일(예약일)
    @Column(nullable = false)
    private LocalDate visitDate;

    //예약 인원수
    @Column(nullable = false)
    private Integer personCount;

    //요청 사항
    @Column(length = 500)
    private String requestNote;


}