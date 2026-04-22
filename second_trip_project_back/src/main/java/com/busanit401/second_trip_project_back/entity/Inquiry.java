package com.busanit401.second_trip_project_back.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Entity
@Table(name = "TRIP_Inquiry") // 👈 이 코드를 추가하세요
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inquiry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String content;
    private String mid;
    private String category;
    private String reply;
    private LocalDateTime regDate;

    // 💡 저장되기 직전에 실행됨
    @PrePersist
    public void prePersist() {
        this.regDate = LocalDateTime.now();
    }
}