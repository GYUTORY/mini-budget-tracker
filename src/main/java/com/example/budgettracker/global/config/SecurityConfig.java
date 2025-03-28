package com.example.budgettracker.global.config;

import com.example.budgettracker.global.dto.ApiResponse;
import com.example.budgettracker.global.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final UserDetailsService userDetailsService;
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
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/check-email").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(exceptionHandling -> exceptionHandling
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    Map<String, Object> body = new HashMap<>();
                    body.put("success", false);
                    body.put("message", "인증되지 않은 사용자입니다.");

                    objectMapper.writeValue(response.getOutputStream(), body);
                })
            );

        // H2 콘솔 사용을 위한 설정
        http.headers(headers -> headers.frameOptions(frameOptions -> frameOptions.disable()));

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}