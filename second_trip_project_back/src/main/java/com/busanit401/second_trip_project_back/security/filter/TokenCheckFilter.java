package com.busanit401.second_trip_project_back.security.filter;

import com.busanit401.second_trip_project_back.security.APIUserDetailsService;
import com.busanit401.second_trip_project_back.security.exception.AccessTokenException;
import com.busanit401.second_trip_project_back.util.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class TokenCheckFilter extends OncePerRequestFilter {

    private final APIUserDetailsService apiUserDetailsService;
    private final JWTUtil jwtUtil;

    // [핵심 로직] 모든 요청마다 실행되어 토큰을 검사하는 메서드
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 로그 출력
        log.info("Token Check Filter triggered...");
        log.info("JWTUtil instance: {}", jwtUtil);


        try {
            // 1. 토큰 유효성 검사: 헤더에서 토큰을 꺼내 진짜인지 확인 (검증 결과는 payload에 담김)
            Map<String, Object> payload = validateAccessToken(request);
            // 2. 사용자 식별: 토큰 안에 들어있던 'mid(아이디)'를 추출
            String mid = (String) payload.get("mid");
            log.info("mid: " + mid);

            // 3. 인증 객체 생성: DB에서 사용자 정보를 불러와서 '인증된 사람'이라는 증명서(Authentication)를 만듦
            UserDetails userDetails = apiUserDetailsService.loadUserByUsername(mid);
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );

            // 4. 보안 컨텍스트에 등록: 서버의 메모리(SecurityContext)에 이 사람 인증됐다고 도장을 찍어줌!
            SecurityContextHolder.getContext().setAuthentication(authentication);
            // 5. 다음 단계로: 검사 끝났으니 다음 필터나 컨트롤러로 보냄
            filterChain.doFilter(request, response);
        } catch (AccessTokenException accessTokenException) {
            // 검증 실패(만료됨, 가짜 토큰 등) 시 에러 응답을 보냄
            accessTokenException.sendResponseError(response);
        }
    }

    // [검문 도구] 실제 토큰이 형식이 맞는지, 위조되진 않았는지 세밀하게 검사
    public Map<String, Object> validateAccessToken(HttpServletRequest request) throws AccessTokenException {
        String headerStr = request.getHeader("Authorization"); // 헤더에서 꺼내기

        // 'Bearer 토큰내용' 형식인지 확인
        // 1. Authorization 헤더가 없는 경우
        if (headerStr == null || headerStr.length() < 8) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.UNACCEPT);
        }

        // 2. 토큰 타입 확인
        String tokenType = headerStr.substring(0, 6);
        String tokenStr = headerStr.substring(7); // "Bearer " 뒷부분만 잘라내기

        if (!tokenType.equalsIgnoreCase("Bearer")) {
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADTYPE);
        }

        try {
            // 3. JWT 검증
            Map<String, Object> values = jwtUtil.validateToken(tokenStr);
            return values;

        } catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException: Invalid token format.");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.MALFORM);

        } catch (SignatureException signatureException) {
            log.error("SignatureException: Invalid token signature.");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.BADSIGN);

        } catch (ExpiredJwtException expiredJwtException) {
            log.error("ExpiredJwtException: Token has expired.");
            throw new AccessTokenException(AccessTokenException.TOKEN_ERROR.EXPIRED);
        }
    }

    // [패스권] 토큰 검사가 필요 없는 '프리패스' 경로를 설정
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        // 회원가입, 로그인, 중복체크 같은 경로는 토큰이 없어도 당연히 통과
        // 아래 경로들로 들어오는 요청은 필터 로직(토큰 검사)을 건너뜁니다.
        if (!path.startsWith("/api/") ||
                path.startsWith("/api/member/exists/") ||
                path.startsWith("/api/member/check-mid") ||
                path.startsWith("/api/member/register") ||
                path.startsWith("/api/member/login") ||
                path.startsWith("/api/member/check-email") ||
                path.startsWith("/api/member/signup") ||
                path.startsWith("/api/airport/flights")) {
            return true; // 필터 검사 안 함 (통과)
        }

        return false;
    }
}

