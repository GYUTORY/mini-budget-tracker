package com.example.budgettracker.domain.auth.controller;

import com.example.budgettracker.domain.auth.dto.LoginRequest;
import com.example.budgettracker.domain.auth.dto.LoginResponse;
import com.example.budgettracker.domain.auth.dto.SignupRequest;
import com.example.budgettracker.domain.auth.dto.SignupResponse;
import com.example.budgettracker.domain.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 API 컨트롤러
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 회원가입 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param request 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signup(
            @Parameter(description = "회원가입 정보", required = true)
            @Valid @RequestBody SignupRequest request) {
        return ResponseEntity.ok(authService.signup(request));
    }

    /**
     * 로그인 API
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param request 로그인 요청 데이터
     * @return 로그인 응답 데이터
     */
    @Operation(summary = "로그인", description = "사용자 인증 및 JWT 토큰 발급")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "로그인 정보", required = true)
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
} 