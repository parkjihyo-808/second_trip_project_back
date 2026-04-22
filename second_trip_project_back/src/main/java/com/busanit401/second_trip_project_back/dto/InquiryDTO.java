package com.busanit401.second_trip_project_back.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InquiryDTO {
    private Long id;
    private String title;
    private String content;
    private String mid; // 작성자 이메일
    private String category;
    private String reply; // 답변 내용
    private LocalDateTime regDate;
}