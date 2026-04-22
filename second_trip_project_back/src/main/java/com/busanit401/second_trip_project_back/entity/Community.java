package com.busanit401.second_trip_project_back.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "trip_community")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Community {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    // 복잡한 거 다 빼고, 그냥 테이블에 있는 'mid' 컬럼만 받자!
    @Column(name = "mid")
    private String mid;
}