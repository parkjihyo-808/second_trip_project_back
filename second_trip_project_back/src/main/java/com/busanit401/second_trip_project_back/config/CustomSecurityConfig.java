package com.busanit401.second_trip_project_back.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@Log4j2
public class CustomSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("------------------- 시큐리티 설정 로드 중 (외부 접속 허용 버전) -------------------");

        // 1. CSRF 완전 비활성화 (가장 확실한 방법)
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. CORS 설정 연결
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 3. 권한 설정 (로그인 없이 가입이 가능하도록 더 명확하게!)
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/member/**").permitAll() // 회원가입, 로그인 등 멤버 경로 허용
                .anyRequest().permitAll() // 나머지 요청도 일단 모두 허용
        );

        // 4. 세션 설정 (나중에 토큰/JWT 쓸 거면 Stateless로 가야 하지만, 일단 기본으로 둬!)

        return http.build();
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