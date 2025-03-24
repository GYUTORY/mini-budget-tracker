package com.example.budgettracker.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 관련 설정을 정의하는 클래스
 */
@Configuration // 이 클래스가 설정 파일임을 명시
@EnableWebSecurity // Spring Security 기능을 활성화 (기본 Web 보안 설정 적용)
public class SecurityConfig {

    /**
     * 비밀번호 암호화를 위한 Bean
     * BCrypt 해싱 알고리즘을 사용해 비밀번호를 안전하게 저장할 수 있음
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 필터 체인을 설정
     * - 어떤 경로에 인증을 요구할지, 어떤 경로를 허용할지를 정의
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(사이트 간 요청 위조) 보호 기능 비활성화
            // REST API에서는 일반적으로 비활성화
            .csrf(csrf -> csrf.disable())

            // HTTP 요청에 대한 인증/인가 규칙 정의
            .authorizeHttpRequests(authorize -> authorize
                // /api/auth/** 경로는 누구나 접근 허용 (회원가입, 로그인 등)
                .requestMatchers("/api/auth/**").permitAll()

                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            );

        // 설정 완료된 SecurityFilterChain 객체를 반환
        return http.build();
    }
}