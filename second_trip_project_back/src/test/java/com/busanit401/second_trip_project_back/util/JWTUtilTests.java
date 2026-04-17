
package com.busanit401.second_trip_project_back.util;

import io.jsonwebtoken.JwtException;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
@Log4j2
public class JWTUtilTests {

    @Autowired
    private JWTUtil jwtUtil;

    /**
     * JWT 토큰 생성 테스트
     */
    @Test
    public void testGenerate() {
        // Claims 데이터 생성
        Map<String, Object> claimMap = Map.of("mid", "ABCDE");

        // 1일 동안 유효한 JWT 생성
        String jwtStr = jwtUtil.generateToken(claimMap, 1);

        // 생성된 JWT 출력
        log.info("Generated JWT: {}", jwtStr);
    }

    @Test
    public void testValidate() {
        // 유효 시간이 지난 토큰
        String jwtStr = "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJtaWQiOiJBQkNERSIsImlhdCI6MTczNzcwMjIzMiwiZXhwIjoxNzM3NzAyMjkyfQ.0SshOFXnpn1mtXfFB2PJexYhfgH3n-70OIU-DWUb-o43";

        try {
            // 토큰 검증 및 Claims 추출
            Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

            // Claims 출력
            log.info("Extracted Claims: {}", claim);
        } catch (JwtException e) {
            // 토큰 검증 실패 처리
            log.error("Token validation failed: {}", e.getMessage());
        }
    }

    @Test
    public void testAll() {
        // JWT 생성
        String jwtStr = jwtUtil.generateToken(
                Map.of("mid", "AAAA", "email", "aaaa@bbb.com"), 1
        );
        log.info("Generated JWT: {}", jwtStr);

        // JWT 검증 및 Claims 추출
        Map<String, Object> claim = jwtUtil.validateToken(jwtStr);

        // Claims 출력
        log.info("MID: {}", claim.get("mid"));
        log.info("EMAIL: {}", claim.get("email"));
    }
}