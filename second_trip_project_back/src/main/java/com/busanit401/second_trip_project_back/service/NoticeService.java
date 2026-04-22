package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.dto.NoticeDTO;
import com.busanit401.second_trip_project_back.entity.Notice;
import com.busanit401.second_trip_project_back.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoticeService {
    private final NoticeRepository repo;

    public List<NoticeDTO> getAllNotices() {
        return repo.findAllByOrderByIdDesc().stream().map(n -> NoticeDTO.builder()
                        .id(n.getId()).title(n.getTitle()).content(n.getContent()).mid(n.getMid()).build())
                .collect(Collectors.toList());
    }

    public NoticeDTO saveNotice(NoticeDTO dto) {
        Notice notice = Notice.builder()
                .title(dto.getTitle()).content(dto.getContent())
                .category(dto.getCategory()).mid(dto.getMid()).build();
        Notice saved = repo.save(notice);
        return NoticeDTO.builder().id(saved.getId()).build();
    }
}