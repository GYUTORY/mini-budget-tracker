package com.example.budgettracker.domain.user.controller;

import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.dto.UpdateProfileRequest;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.service.UserService;
import com.example.budgettracker.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.HashMap;

/**
 * 사용자 관련 API를 제공하는 컨트롤러
 * 
 * 주요 기능:
 * - 이메일 중복 확인 (/api/user/check-email)
 * - 로그아웃 (/api/user/logout)
 * - 프로필 조회 (/api/user/me)
 * - 프로필 수정 (/api/user/profile)
 * - 사용자 정보 수정 (/api/user/me)
 * 
 * 보안:
 * - 대부분의 엔드포인트는 인증 필요
 * - JWT 토큰 기반 인증 사용
 * - 입력값 검증 (@Valid) 적용
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "사용자", description = "사용자 관련 API")
@RestController // 이 클래스가 REST API 컨트롤러임을 명시 (JSON 반환)
@RequestMapping("/api/users") // 이 컨트롤러의 공통 URL Prefix 지정
@RequiredArgsConstructor // Lombok: final 필드를 자동으로 생성자 주입
public class UserController {

    private final UserService userService; // 회원가입 로직을 처리할 서비스 클래스 주입

    /**
     * 이메일 중복 확인 API
     * 
     * 기능:
     * - 회원가입 전 이메일 중복 여부 확인
     * - 데이터베이스에서 이메일 존재 여부 검사
     * 
     * 요청 데이터:
     * - email: 확인할 이메일 주소
     * 
     * 응답 데이터:
     * - success: 성공 여부
     * - data: 이메일 사용 가능 여부 (true/false)
     * - message: 결과 메시지
     * 
     * @Operation: API 엔드포인트 설명
     * @ApiResponses: 가능한 응답 코드와 설명
     * @param request 이메일 정보가 포함된 요청 데이터
     * @return 이메일 중복 확인 결과
     */
    @Operation(summary = "이메일 중복 확인", description = "회원가입 시 이메일 중복 여부를 확인합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 중복 확인 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(
            @Parameter(description = "이메일 정보", required = true)
            @RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isAvailable = !userService.existsByEmail(email);
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    /**
     * 로그아웃 API
     * 
     * 기능:
     * - 현재 사용자의 JWT 토큰을 무효화
     * - Redis에서 토큰 정보 제거
     * 
     * 보안:
     * - 인증된 사용자만 접근 가능
     * - JWT 토큰 필요
     * 
     * 응답 데이터:
     * - success: 성공 여부
     * - message: 로그아웃 성공 메시지
     * 
     * @Operation: API 엔드포인트 설명
     * @SecurityRequirement: JWT 토큰 필요 표시
     * @ApiResponses: 가능한 응답 코드와 설명
     * @return 로그아웃 결과
     */
    @Operation(summary = "로그아웃", description = "현재 사용자의 로그아웃을 처리합니다.")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그아웃 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 현재 인증된 사용자의 토큰을 무효화
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃이 완료되었습니다."));
    }

    /**
     * 프로필 조회 API
     * 
     * 기능:
     * - 현재 로그인한 사용자의 프로필 정보 조회
     * - 이메일, 이름 등의 기본 정보 제공
     * 
     * 보안:
     * - 인증된 사용자만 접근 가능
     * - JWT 토큰 필요
     * 
     * 응답 데이터:
     * - success: 성공 여부
     * - email: 사용자 이메일
     * - name: 사용자 이름
     * 
     * @Operation: API 엔드포인트 설명
     * @SecurityRequirement: JWT 토큰 필요 표시
     * @ApiResponses: 가능한 응답 코드와 설명
     * @param userDetails 현재 인증된 사용자 정보
     * @return 사용자 프로필 정보
     */
    @Operation(summary = "프로필 조회", description = "현재 로그인한 사용자의 프로필 정보를 조회합니다.")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 조회 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.getProfile(userDetails.getUsername());
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("email", user.getEmail());
        response.put("name", user.getName());
        return ResponseEntity.ok(response);
    }

    /**
     * 프로필 수정 API
     * 
     * 기능:
     * - 현재 로그인한 사용자의 프로필 정보 수정
     * - 이름, 비밀번호 등 수정 가능
     * 
     * 보안:
     * - 인증된 사용자만 접근 가능
     * - JWT 토큰 필요
     * - 입력값 검증 적용
     * 
     * 요청 데이터:
     * - name: 새로운 이름 (선택)
     * - password: 새로운 비밀번호 (선택)
     * 
     * 응답 데이터:
     * - success: 성공 여부
     * - email: 수정된 이메일
     * - name: 수정된 이름
     * 
     * @Operation: API 엔드포인트 설명
     * @SecurityRequirement: JWT 토큰 필요 표시
     * @ApiResponses: 가능한 응답 코드와 설명
     * @param request 수정할 프로필 정보
     * @return 수정된 프로필 정보
     */
    @Operation(summary = "프로필 수정", description = "현재 로그인한 사용자의 프로필 정보를 수정합니다.")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "프로필 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<SignupResponse>> updateProfile(
            @Parameter(description = "수정할 프로필 정보", required = true)
            @Valid @RequestBody UpdateProfileRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        SignupResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "프로필이 성공적으로 수정되었습니다."));
    }

    @Operation(summary = "사용자 정보 수정", description = "현재 로그인한 사용자의 정보를 수정합니다.")
    @SecurityRequirement(name = "bearer-key")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없음")
    })
    @PutMapping("/me")
    public ResponseEntity<Void> updateUserInfo(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "수정할 사용자 정보", required = true)
            @Valid @RequestBody SignupRequest request) {
        String userId = authentication.getName();
        userService.updateUserInfo(userId, request);
        return ResponseEntity.ok().build();
    }
}