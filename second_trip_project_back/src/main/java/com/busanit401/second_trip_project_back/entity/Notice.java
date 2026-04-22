package com.busanit401.second_trip_project_back.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String content;
    private String category;
    private String mid;
    private String reply;
    private String tag;
    private LocalDateTime regDate;

    @PrePersist
    public void prePersist() { this.regDate = LocalDateTime.now(); }
}