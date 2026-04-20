package com.busanit401.second_trip_project_back.domain.packages;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "trip_package_reservation")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString(exclude = {"member", "packageItem"})
public class PackageReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member 엔티티와 연결 (PK인 id를 참조)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // PackageItem 엔티티와 연결
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "package_id")
    private PackageItem packageItem;

    @Column(nullable = false)
    private LocalDate reservationDate;

    @Column(nullable = false)
    private int peopleCount;

    @Column(nullable = false)
    private int totalPrice;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status; // PENDING, CONFIRMED 등
}