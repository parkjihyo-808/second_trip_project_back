package com.busanit401.second_trip_project_back.service;


import com.busanit401.second_trip_project_back.dto.InquiryDTO;
import com.busanit401.second_trip_project_back.entity.Inquiry;
import com.busanit401.second_trip_project_back.repository.InquiryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InquiryService {
    private final InquiryRepository inquiryRepository;

    // 특정 사용자의 문의 목록만 조회 (로그인된 사용자 이메일 기준)
    public List<InquiryDTO> getList(String mid) {
        List<Inquiry> inquiryList = inquiryRepository.findByMidOrderByRegDateDesc(mid);

        return inquiryList.stream().map(inquiry -> InquiryDTO.builder()
                .id(inquiry.getId())
                .title(inquiry.getTitle())
                .content(inquiry.getContent())
                .mid(inquiry.getMid())
                .category(inquiry.getCategory())
                .reply(inquiry.getReply())
                .regDate(inquiry.getRegDate())
                .build()).collect(Collectors.toList());
    }
}