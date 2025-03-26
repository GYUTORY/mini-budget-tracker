package com.example.budgettracker.global.config;

import com.example.budgettracker.global.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

/**
 * Spring Security 관련 설정을 정의하는 클래스
 * 
 * @Configuration: 이 클래스가 Spring 설정 클래스임을 명시
 * - Spring이 이 클래스를 빈 설정 소스로 인식
 * - @Bean 어노테이션이 붙은 메서드들을 통해 빈을 정의
 * 
 * @EnableWebSecurity: Spring Security의 웹 보안 기능을 활성화
 * - 기본 보안 설정을 비활성화하고 커스텀 설정을 적용
 * - WebSecurityConfigurerAdapter를 상속받지 않고도 설정 가능
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final ObjectMapper objectMapper;

    /**
     * 비밀번호 암호화를 위한 Bean
     * 
     * @Bean: Spring이 이 메서드의 반환값을 빈으로 등록
     * - 메서드 이름이 빈의 이름이 됨
     * - 싱글톤으로 관리됨
     * 
     * BCryptPasswordEncoder: 비밀번호 암호화에 사용되는 인코더
     * - BCrypt 해싱 알고리즘 사용
     * - 자동으로 salt를 생성하고 관리
     * - Rainbow Table 공격에 대한 방어 기능 제공
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Security 필터 체인을 설정하는 Bean
     * 
     * @Bean: Spring이 이 메서드의 반환값을 빈으로 등록
     * - SecurityFilterChain 타입의 빈이 등록됨
     * - Spring Security의 필터 체인을 구성
     * 
     * @param http: HttpSecurity 객체
     * - Spring Security의 웹 보안 설정을 위한 객체
     * - 체이닝 방식으로 설정을 추가할 수 있음
     * 
     * @return SecurityFilterChain: 구성된 보안 필터 체인
     * - Spring Security의 모든 보안 설정이 적용된 필터 체인
     * 
     * @throws Exception: 보안 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF(Cross-Site Request Forgery) 보호 비활성화
            // - API 서버는 CSRF 토큰이 필요 없음
            // - 세션 기반 인증을 사용하지 않음
            .csrf(AbstractHttpConfigurer::disable)
            
            // 세션 관리 설정
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // HTTP 요청에 대한 권한 설정
            .authorizeHttpRequests(auth -> auth
                // /api/auth/signup, /api/auth/login, /api/auth/check-email 경로는 인증 없이 접근 가능
                // - 회원가입, 로그인, 이메일 확인 API
                .requestMatchers("/api/auth/signup", "/api/auth/login", "/api/auth/check-email").permitAll()
                
                // 그 외 모든 요청은 인증 필요
                // - 로그인한 사용자만 접근 가능
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response) -> {
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResponse.error("401", "Authorization 토큰이 유효하지 않습니다.")
                    ));
                })
                .accessDeniedHandler((request, response) -> {
                    response.setStatus(HttpStatus.FORBIDDEN.value());
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.getWriter().write(objectMapper.writeValueAsString(
                        ApiResponse.error("403", "접근 권한이 없습니다.")
                    ));
                })
            );
        
        // 설정된 HttpSecurity 객체를 SecurityFilterChain으로 변환하여 반환
        return http.build();
    }
}