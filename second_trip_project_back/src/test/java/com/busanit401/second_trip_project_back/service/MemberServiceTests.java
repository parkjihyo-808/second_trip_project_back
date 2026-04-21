package com.busanit401.second_trip_project_back.service;

import com.busanit401.second_trip_project_back.service.member.MemberService;
import com.busanit401.second_trip_project_back.dto.MemberDTO;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Log4j2
public class MemberServiceTests {

    @Autowired
    private MemberService memberService;

    @Test
    public void testRegister() {
        MemberDTO memberDTO = MemberDTO.builder()
                .mid("test_user2@travel.com") // 👈 숫자 2를 붙여보자!
                .mpw("5555")
                .mname("테스트유저2")
                .email("test_user2@travel.com") // 👈 이메일도 중복 안 되게!
                .phone("010-0000-0000")
                .build();

        memberService.register(memberDTO);
        log.info("회원가입 서비스 테스트 성공!");
    }

    @Test
    public void testRead() {
        // 이미 DB에 있는 아이디로 조회해야 에러가 안 나!
        // 아까 관리자 계정 만들었지? 그걸로 테스트해보자.
        String mid = "admin@test.com"; // 👈 여기를 확실히 있는 아이디로!

        try {
            MemberDTO memberDTO = memberService.read(mid);
            log.info("---------------------------");
            log.info("조회 성공! 이름: " + memberDTO.getMname());
            log.info("권한: " + memberDTO.getRole());
            log.info("---------------------------");
        } catch (Exception e) {
            log.error("조회 실패 ㅠㅠ 아이디가 DB에 없는 것 같아!");
        }
    }
}