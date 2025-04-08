package com.example.budgettracker.domain.auth.controller;

import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 인증 관련 API를 제공하는 컨트롤러
 * 
 * 주요 기능:
 * - 회원가입 (/api/auth/signup)
 * - 로그인 (/api/auth/login)
 * 
 * 보안:
 * - 모든 엔드포인트는 인증 없이 접근 가능
 * - 입력값 검증 (@Valid) 적용
 * - JWT 토큰 기반 인증 사용
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "인증", description = "인증 관련 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;

    /**
     * 회원가입 API
     * 
     * 기능:
     * - 새로운 사용자 등록
     * - 이메일 중복 검사
     * - 비밀번호 암호화
     * 
     * 요청 데이터:
     * - email: 사용자 이메일 (필수, 이메일 형식)
     * - password: 비밀번호 (필수, 8자 이상)
     * - name: 사용자 이름 (필수)
     * 
     * 응답 데이터:
     * - success: 성공 여부
     * - email: 등록된 이메일
     * - name: 등록된 이름
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param request 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     */
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/signup")
    public ResponseEntity<Map<String, Object>> signup(@Valid @RequestBody SignupRequest request) {
        User user = userService.signup(request);
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 로그인 API
     * 
     * 기능:
     * - 사용자 인증
     * - JWT 토큰 발급
     * 
     * 요청 데이터:
     * - email: 사용자 이메일
     * - password: 비밀번호
     * 
     * 응답 데이터:
     * - token: JWT 토큰
     * - type: 토큰 타입 (Bearer)
     * - email: 사용자 이메일
     * - name: 사용자 이름
     * 
     * @Operation: API 엔드포인트 설명
     * @Parameter: API 파라미터 설명
     * @param request 로그인 요청 데이터
     * @return 로그인 응답 데이터
     */
    @Operation(summary = "로그인", description = "사용자 인증을 수행하고 JWT 토큰을 발급합니다.")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Parameter(description = "로그인 정보", required = true)
            @Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(userService.login(request));
    }
} 