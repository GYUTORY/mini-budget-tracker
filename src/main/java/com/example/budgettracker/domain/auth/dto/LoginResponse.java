package com.example.budgettracker.domain.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 응답 정보를 담는 DTO (Data Transfer Object)
 * 로그인 성공 시 클라이언트에게 전달할 정보를 담습니다.
 */
@Getter // Lombok: 모든 필드의 Getter 메서드 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
@Builder
@Schema(description = "로그인 응답")
public class LoginResponse {

    @Schema(description = "JWT 토큰", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token; // JWT 인증 토큰

    @Schema(description = "응답 메시지", example = "로그인이 완료되었습니다.")
    private String message;

    /**
     * 로그인 응답 객체 생성을 위한 빌더 메서드
     * 
     * @param token JWT 인증 토큰
     */
    @Builder // Lombok: 빌더 패턴 구현체 자동 생성
    public LoginResponse(String token, String message) {
        this.token = token;
        this.message = message;
    }
} 