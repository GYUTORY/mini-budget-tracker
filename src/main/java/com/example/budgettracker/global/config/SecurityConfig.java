package com.example.budgettracker.global.config;

import com.example.budgettracker.global.security.CustomUserDetailsService;
import com.example.budgettracker.global.config.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정 클래스
 * 
 * @Configuration: 설정 클래스임을 나타냄
 * @EnableWebSecurity: Spring Security 웹 보안 활성화
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * SecurityFilterChain 빈 설정
     * 
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)  // CSRF 보호 비활성화 (JWT 사용)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/users/signup", "/api/users/login", "/api/users/check-email").permitAll()
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**",
                    "/swagger-resources/**",
                    "/webjars/**"
                ).permitAll()
                .anyRequest().authenticated()  // 그 외 모든 요청은 인증 필요
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)  // 세션 사용하지 않음
            )
            .authenticationProvider(authenticationProvider())  // 인증 제공자 설정
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);  // JWT 필터 추가

        return http.build();
    }

    /**
     * AuthenticationProvider 빈 설정
     * 
     * @return DaoAuthenticationProvider 객체
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * AuthenticationManager 빈 설정
     * 
     * @param config AuthenticationConfiguration 객체
     * @return AuthenticationManager 객체
     * @throws Exception 설정 중 발생할 수 있는 예외
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * PasswordEncoder 빈 설정
     * 
     * @return BCryptPasswordEncoder 객체
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}