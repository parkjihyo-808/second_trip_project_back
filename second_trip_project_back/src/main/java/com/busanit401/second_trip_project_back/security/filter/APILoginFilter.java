package com.busanit401.second_trip_project_back.security.filter;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Map;

@Log4j2
public class APILoginFilter extends AbstractAuthenticationProcessingFilter {

    // [설정] CustomSecurityConfig에서 설정한 "/generateToken" 주소로 요청이 올 때만 이 필터가 작동
    public APILoginFilter(String defaultFilterProcessesUrl) {
        super(defaultFilterProcessesUrl); // 로그인 엔드포인트 설정
    }

    // [핵심 로직] 로그인을 시도하는 메서드
    @Override
    public Authentication attemptAuthentication(
            HttpServletRequest request,
            HttpServletResponse response
    ) throws IOException, ServletException {
        log.info("APILoginFilter - attemptAuthentication called");

        // GET 요청은 지원하지 않음
        // 1. 보안 체크: 로그인 정보는 민감하니까 GET 방식은 거절하고 POST만 받겠다는 뜻
        if (request.getMethod().equalsIgnoreCase("GET")) {
            log.info("GET METHOD NOT SUPPORTED");
            return null;
        }

        // 2. 데이터 추출: 플러터에서 보낸 JSON 데이터(id, pw)를 꺼내서 Map으로 변환
        // JSON 데이터 파싱
        Map<String, String> jsonData = parseRequestJSON(request);
        log.info("Parsed JSON Data: {}", jsonData);

        // 3. 인증 토큰 생성: 꺼낸 아이디와 비번을 '인증용 티켓(Token)' 박스에 담는 과정
        // TODO: 인증 로직 추가
        // JSON 데이터에서 사용자 ID와 비밀번호를 추출하여 인증 토큰 생성
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        jsonData.get("mid"), // 사용자 ID
                        jsonData.get("mpw")  // 사용자 비밀번호
                );

        // 4. 인증 위임: "내가 아이디랑 비번 담아왔으니까, 실제 DB랑 맞는지 확인해줘."라고 매니저한테 넘기는 것
        // AuthenticationManager를 사용하여 인증 시도
        return getAuthenticationManager().authenticate(authenticationToken);
    }

    // [보조 메서드] 플러터에서 보낸 복잡한 JSON 문자열을 자바가 이해하기 쉽게 Map으로 바꿔주는 도구
    private Map<String, String> parseRequestJSON(HttpServletRequest request) {
        // JSON 데이터를 파싱하여 mid와 mpw 값을 Map으로 처리
        try (Reader reader = new InputStreamReader(request.getInputStream())) {
            Gson gson = new Gson(); // 구글의 JSON 처리 라이브러리 활용
            return gson.fromJson(reader, Map.class);
        } catch (Exception e) {
            log.error("Error parsing JSON request: {}", e.getMessage());
        }
        return null;
    }
}