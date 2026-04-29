package com.busanit401.second_trip_project_back.config;

import com.busanit401.second_trip_project_back.security.APIUserDetailsService;
import com.busanit401.second_trip_project_back.security.filter.APILoginFilter;
import com.busanit401.second_trip_project_back.security.filter.RefreshTokenFilter;
import com.busanit401.second_trip_project_back.security.filter.TokenCheckFilter;
import com.busanit401.second_trip_project_back.security.handler.APILoginSuccessHandler;
import com.busanit401.second_trip_project_back.util.JWTUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableMethodSecurity
@EnableWebSecurity
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {
    private final APIUserDetailsService apiUserDetailsService;
    private final JWTUtil jwtUtil;  //jwt생성 및 관리

    // [회원가입/로그인 관련] 비밀번호를 안전하게 암호화하는 도구 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        log.info("시큐리티 동작 확인 ====webSecurityCustomizer======================");
        return (web) ->
                web.ignoring()
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations());  //정적 리소스는 필터에서 무시(css, js, 이미지 등등), 검사 안한다는 뜻
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("------------------- 시큐리티 설정 로드 중 (외부 접속 허용 버전) -------------------");

        // [로그인 관련] 인증을 담당할 매니저 설정
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        // AuthenticationManagerBuilder에 UserDetailsService와 PasswordEncoder 설정
        authenticationManagerBuilder
                .userDetailsService(apiUserDetailsService) // 유저 정보를 DB에서 가져오는 서비스 연결
                .passwordEncoder(passwordEncoder()); // 입력한 비번과 DB 비번을 비교할 때 사용할 암호화기 설정

        // AuthenticationManager 생성
        AuthenticationManager authenticationManager =
                authenticationManagerBuilder.build();

        // AuthenticationManager를 HttpSecurity에 설정
        http.authenticationManager(authenticationManager); // 반드시 필요: Security 필터 체인에서 사용할 AuthenticationManager 설정

        // [로그인 관련] "/generateToken" 주소로 요청이 오면 실행될 '로그인 필터' 설정
        APILoginFilter apiLoginFilter = new APILoginFilter("/generateToken"); // 로그인 포스트 엔드포인트 설정
        apiLoginFilter.setAuthenticationManager(authenticationManager); // APILoginFilter에서 사용할 AuthenticationManager 설정

        // [로그인 관련] 로그인 성공 시 JWT 토큰을 발급해주는 '성공 핸들러' 연결
        // APILoginSuccessHandler 생성: 인증 성공 후 처리 로직을 담당
        APILoginSuccessHandler successHandler = new APILoginSuccessHandler(jwtUtil);    //성공하면 키생성해서 응답하는 핸들러
        // SuccessHandler 설정: 로그인 성공 시 APILoginSuccessHandler가 호출되도록 설정
        apiLoginFilter.setAuthenticationSuccessHandler(successHandler); //로그인 필터에서 성공시 응답할 핸들러 등록

        // [인증 관련] 요청마다 토큰이 유효한지 검사하는 필터들 순서대로 배치
        http.addFilterBefore(apiLoginFilter, UsernamePasswordAuthenticationFilter.class); // 사용자 인증 전에 APILoginFilter 동작 설정
        http.addFilterBefore(
                tokenCheckFilter(jwtUtil, apiUserDetailsService),
                UsernamePasswordAuthenticationFilter.class
        );
        // RefreshTokenFilter를 TokenCheckFilter 이전에 등록
        http.addFilterBefore(
                new RefreshTokenFilter("/refreshToken", jwtUtil),
                TokenCheckFilter.class
        );

        // [보안 설정] REST API(앱 통신)이므로 CSRF는 끄고, CORS는 허용 설정
        // 1. CSRF 완전 비활성화 (가장 확실한 방법)
        http.csrf(AbstractHttpConfigurer::disable);
        // 2. CORS 설정 연결
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // [회원가입 관련] 인증 없이 접근 가능한 경로 설정
        // 3. 권한 설정 (로그인 없이 가입이 가능하도록 더 명확하게!)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/member/**").permitAll() // 회원가입, 로그인 등 멤버 경로 허용 // ⭐ 여기 덕분에 로그인 안 한 상태에서도 '회원가입' 주소에 접근 가능
                .requestMatchers("/api/tours/**").permitAll()
                .requestMatchers("/car/**").permitAll()
                .requestMatchers("/swagger-ui/**","/v3/api-docs/**","swagger-resources/**", "/webjars/**").permitAll()
//                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                .anyRequest().permitAll() // 모든 요청 허용
        );

        // [인증 방식] 세션을 쓰지 않고 토큰을 쓰는 'Stateless' 방식으로 설정 (앱 최적화)
        // 4. 세션 설정
        http.sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        return http.build();
    }

    private TokenCheckFilter tokenCheckFilter(JWTUtil jwtUtil, APIUserDetailsService apiUserDetailsService){
        return new TokenCheckFilter(apiUserDetailsService, jwtUtil);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // ⭐ 모든 도메인에서 오는 요청을 허용 (플러터 웹이나 다른 브라우저 대응)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // ⭐ 헤더와 메서드 허용 범위를 조금 더 넓게 잡아주자
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));

        // ⭐ 인증 정보(쿠키 등)를 포함한 요청 허용
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 적용
        return source;
    }
}