package com.example.budgettracker.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 정보를 담는 DTO (Data Transfer Object)
 * 로그인 성공 시 클라이언트에게 전달할 정보를 담습니다.
 */
@Getter // Lombok: 모든 필드의 Getter 메서드 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
public class LoginResponse {

    private String token; // JWT 인증 토큰

    /**
     * 로그인 응답 객체 생성을 위한 빌더 메서드
     * 
     * @param token JWT 인증 토큰
     */
    @Builder // Lombok: 빌더 패턴 구현체 자동 생성
    public LoginResponse(String token) {
        this.token = token;
    }
} 