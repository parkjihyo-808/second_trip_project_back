package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.member.MemberRole;
import com.busanit401.second_trip_project_back.dto.MemberDTO;
import com.busanit401.second_trip_project_back.repository.MemberRepository;
import com.busanit401.second_trip_project_back.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@Log4j2
@RequiredArgsConstructor
@Transactional
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil; // ⭐ 1. 토큰 기계 소환! (팀원분이 만든 클래스 이름 확인해줘)

    @Override
    public void register(MemberDTO memberDTO) {

    }

    @Override
    public MemberDTO read(String mid) {
        return null;
    }

    @Override
    public void modify(MemberDTO memberDTO) {

    }

    @Override
    public void remove(String mid) {

    }

    @Override
    public MemberDTO login(String mid, String mpw) {
        Member member = memberRepository.findByMid(mid)
                .orElseThrow(() -> new RuntimeException("아이디가 존재하지 않습니다."));

        if (!passwordEncoder.matches(mpw, member.getMpw())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 2. 일단 정보를 봉투에 담아!
        MemberDTO memberDTO = entityToDTO(member);

        // ⭐ 3. [핵심!] 여기서 팔찌를 찍어서 봉투에 쏙 넣어줘야 해!
        // jwtUtil.generateToken의 사용법은 팀원분이 짠 코드에 따라 다를 수 있어!
        String token = jwtUtil.generateToken(Map.of("mid", mid), 1); // 예시: 1일짜리 토큰
        memberDTO.setAccessToken(token); // 👈 이제 'null' 탈출!

        return memberDTO;
    }

    @Override
    public Optional<Member> findByMid(String mid) {
        return Optional.empty();
    }

    @Override
    public boolean existsByMid(String mid) {
        return false;
    }

    @Override
    public Optional<Member> findByEmail(String email) {
        return Optional.empty();
    }

    // 4. 봉투 만드는 기계도 업데이트!
    private MemberDTO entityToDTO(Member member) {
        return MemberDTO.builder()
                .mid(member.getMid())
                .mname(member.getMname())
                .email(member.getEmail())
                .phone(member.getPhone())
                .role(member.getRole().name())
                .regDate(member.getRegDate())
                // .accessToken(null) // 처음엔 비어있지만 나중에 login에서 채울 거야!
                .build();
    }
}