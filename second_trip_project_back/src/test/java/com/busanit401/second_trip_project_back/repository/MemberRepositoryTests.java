package com.busanit401.second_trip_project_back.repository;

import com.busanit401.second_trip_project_back.domain.member.Member;
import com.busanit401.second_trip_project_back.domain.member.MemberRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

@SpringBootTest
public class MemberRepositoryTests {

    @Autowired
    private MemberRepository memberRepository;

    @Test
    public void insertAdminTest() {
        // 1. 관리자(ADMIN) 계정 정보 세팅
        Member admin = Member.builder()
                .mid("admin@test.com") // 로그인 아이디
                .mpw("1234")           // 비밀번호 (나중에 암호화 할거야!)
                .mname("트래블허브관리자")
                .email("admin@test.com")
                .phone("010-1234-5678")
                .role(MemberRole.ADMIN) // ⭐ 여기가 제일 중요! ADMIN으로 설정
                .build();

        // 2. DB에 저장!
        memberRepository.save(admin);

        System.out.println("관리자 계정이 생성됐습니다.");
    }

    @Test
    public void testRead() {
        // 잘 들어갔는지 아이디로 한번 찾아보는 테스트야
        Optional<Member> result = memberRepository.findByMid("admin@test.com");
        Member member = result.orElseThrow();

        System.out.println("찾은 회원 이름: " + member.getMname());
        System.out.println("회원 권한: " + member.getRole());
    }
}