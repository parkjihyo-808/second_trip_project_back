package com.busanit401.second_trip_project_back.security.filter;

import com.busanit401.second_trip_project_back.security.exception.RefreshTokenException;
import com.busanit401.second_trip_project_back.util.JWTUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Log4j2
@RequiredArgsConstructor
public class RefreshTokenFilter  extends OncePerRequestFilter {

    private final String refreshPath; // "/refreshToken" 주소 감시
    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        log.info("lsy path refresh token filter..... : " + path);
        // 1. 경로 체크: 요청 주소가 "/refreshToken"이 아니면 그냥 통과
        if (!path.equals(refreshPath)) {
            log.info("lsy skip refresh token filter....refreshPath : ." + refreshPath);
            log.info("lsy skip refresh token filter.....");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("lsy Refresh Token Filter...run..............1");

        // 2. 데이터 추출: 플러터에서 보낸 만료된 AccessToken과 아직 살아있는 RefreshToken을 읽어와
        Map<String, String> tokens = parseRequestJSON(request);
        String accessToken = tokens.get("accessToken");
        String refreshToken = tokens.get("refreshToken");

        log.info("lsy accessToken: " + accessToken);
        log.info("lsy refreshToken: " + refreshToken);

        // 3. Access Token 검사: 토큰 자체가 아예 없는 건 아닌지 확인해.
        try{
            checkAccessToken(accessToken);
        }catch(RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return;
        }

        // 4. Refresh Token 검사: 이게 만료되면 진짜 다시 로그인
        Map<String, Object> refreshClaims = null;
        try {

            refreshClaims = checkRefreshToken(refreshToken);
            log.info(refreshClaims);

        }catch(RefreshTokenException refreshTokenException){
            refreshTokenException.sendResponseError(response);
            return;
        }

        // 5. 토큰 갱신 로직: Refresh Token의 남은 시간 계산
        Integer exp = (Integer)refreshClaims.get("exp"); // 만료 시간
        Date expTime = new Date(Instant.ofEpochMilli(exp).toEpochMilli() * 1000);
        Date current = new Date(System.currentTimeMillis());

        //만료 시간과 현재 시간의 간격 계산
        //만일 3일 미만인 경우에는 Refresh Token도 다시 생성
        long gapTime = (expTime.getTime() - current.getTime());

        log.info("-----------------------------------------");
        log.info("current: " + current);
        log.info("expTime: " + expTime);
        log.info("gap: " + gapTime );

        String username = (String)refreshClaims.get("mid");
        log.info("username: " + username);

        // [결과 1] AccessToken은 무조건 새로 발급
        String accessTokenValue = jwtUtil.generateToken(Map.of("username", username), 1);
        String refreshTokenValue = tokens.get("refreshToken");

        //RefrshToken이 3분도 안남았다면..
//        if(gapTime < (1000 * 60  * 3  ) ){
        //RefrshToken이 3일도 안남았다면..
        // [결과 2] RefreshToken이 3일도 안 남았다면 이것도 새로 발급해서 연장
        if(gapTime < (1000 * 60 * 60 * 24 * 3  ) ){
            log.info("new Refresh Token required...  ");
            refreshTokenValue = jwtUtil.generateToken(Map.of("username", username), 3);
        }

        log.info("Refresh Token result....................");
        log.info("accessToken: " + accessTokenValue);
        log.info("refreshToken: " + refreshTokenValue);

        // 6. 응답: 새로 만든 토큰들을 플러터에게 다시 보냄
        sendTokens(accessTokenValue, refreshTokenValue, response);


    }

    // ... 이하 토큰 검사 및 전송 보조 메서드들
    private Map<String,String> parseRequestJSON(HttpServletRequest request) {

        //JSON 데이터를 분석해서 mid, mpw 전달 값을 Map으로 처리
        try(Reader reader = new InputStreamReader(request.getInputStream())){

            Gson gson = new Gson();

            return gson.fromJson(reader, Map.class);

        }catch(Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    private void checkAccessToken(String accessToken)throws RefreshTokenException {

        try{
            jwtUtil.validateToken(accessToken);
        }catch (ExpiredJwtException expiredJwtException){
            log.info("Access Token has expired");
        }catch(Exception exception){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_ACCESS);
        }
    }

    private Map<String, Object> checkRefreshToken(String refreshToken)throws RefreshTokenException{

        try {
            Map<String, Object> values = jwtUtil.validateToken(refreshToken);

            return values;

        }catch(ExpiredJwtException expiredJwtException){
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.OLD_REFRESH);
        }catch (MalformedJwtException malformedJwtException) {
            log.error("MalformedJwtException============================== ");
            throw new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        catch(Exception exception){
            exception.printStackTrace();
            new RefreshTokenException(RefreshTokenException.ErrorCase.NO_REFRESH);
        }
        return null;
    }

    private void sendTokens(String accessTokenValue, String refreshTokenValue, HttpServletResponse response) {


        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Gson gson = new Gson();

        String jsonStr = gson.toJson(Map.of("accessToken", accessTokenValue,
                "refreshToken", refreshTokenValue));

        try {
            response.getWriter().println(jsonStr);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}