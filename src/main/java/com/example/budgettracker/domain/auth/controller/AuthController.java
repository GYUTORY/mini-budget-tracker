package com.example.budgettracker.domain.auth.controller;

import com.example.budgettracker.domain.auth.dto.LoginRequest;
import com.example.budgettracker.domain.auth.dto.LoginResponse;
import com.example.budgettracker.domain.auth.service.AuthService;
import com.example.budgettracker.global.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 인증 관련 요청을 처리하는 컨트롤러
 * 로그인, 로그아웃 등의 인증 관련 API를 제공합니다.
 */
@RestController // JSON 형태로 응답을 반환하는 REST 컨트롤러임을 명시
@RequestMapping("/api/auth") // 기본 URL 경로 설정
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
public class AuthController {

    private final AuthService authService; // 인증 관련 비즈니스 로직을 처리하는 서비스

    /**
     * 사용자 로그인을 처리하는 엔드포인트
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return 로그인 성공 시 JWT 토큰을 포함한 응답
     */
    @PostMapping("/login") // POST /api/auth/login
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ApiResponse.success(response, "로그인이 완료되었습니다.");
    }
} 