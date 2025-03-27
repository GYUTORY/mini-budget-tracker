package com.example.budgettracker.domain.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 로그인 요청 정보를 담는 DTO (Data Transfer Object)
 * 클라이언트로부터 전달받은 로그인 정보를 담습니다.
 */
@Getter // Lombok: 모든 필드의 Getter 메서드 자동 생성
@NoArgsConstructor // Lombok: 기본 생성자 자동 생성
public class LoginRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.") // 빈 값이나 공백만 있는 경우 검증 실패
    @Email(message = "이메일 형식이 올바르지 않습니다.") // 이메일 형식 검증
    private String email; // 사용자 이메일

    @NotBlank(message = "비밀번호는 필수 입력값입니다.") // 빈 값이나 공백만 있는 경우 검증 실패
    private String password; // 사용자 비밀번호

    /**
     * 로그인 요청 객체 생성을 위한 빌더 메서드
     * 
     * @param email 사용자 이메일
     * @param password 사용자 비밀번호
     */
    @Builder // Lombok: 빌더 패턴 구현체 자동 생성
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
} 