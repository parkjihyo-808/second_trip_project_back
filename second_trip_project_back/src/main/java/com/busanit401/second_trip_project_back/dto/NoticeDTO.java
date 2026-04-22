package com.busanit401.second_trip_project_back.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDTO {
    private Long id;
    private String title;
    private String content;
    private String mid;
    private String category;
    private String reply;
    private String tag;
    private LocalDateTime regDate;
}