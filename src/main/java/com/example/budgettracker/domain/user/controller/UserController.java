package com.example.budgettracker.domain.user.controller;

import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.dto.UpdateProfileRequest;
import com.example.budgettracker.domain.user.service.UserService;
import com.example.budgettracker.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 사용자 인증 관련 API를 제공하는 컨트롤러
 */
@RestController // 이 클래스가 REST API 컨트롤러임을 명시 (JSON 반환)
@RequestMapping("/api/users") // 이 컨트롤러의 공통 URL Prefix 지정
@RequiredArgsConstructor // Lombok: final 필드를 자동으로 생성자 주입
public class UserController {

    private final UserService userService; // 회원가입 로직을 처리할 서비스 클래스 주입

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        boolean isAvailable = !userService.existsByEmail(email);
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    /**
     * 회원가입 API
     * POST /api/users/signup
     *
     * @param request 클라이언트가 보낸 회원가입 요청 DTO
     * @return 회원가입 결과 DTO와 201(CREATED) 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        SignupResponse response = userService.signup(request);
        return ResponseEntity.ok(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }

    /**
     * 로그인 API
     * POST /api/auth/login
     *
     * @param request 로그인 요청 정보
     * @return 로그인 결과
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest request) {
        LoginResponse response = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "로그인이 완료되었습니다."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // 현재 인증된 사용자의 토큰을 무효화
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        userService.logout(userId);
        return ResponseEntity.ok(ApiResponse.success(null, "로그아웃이 완료되었습니다."));
    }

    /**
     * 마이페이지 조회 API
     * GET /api/users/me
     *
     * @return 사용자 정보
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<SignupResponse>> getMyPage() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        SignupResponse response = userService.getUserInfo(userId);
        return ResponseEntity.ok(ApiResponse.success(response, "마이페이지 조회가 완료되었습니다."));
    }

    /**
     * 프로필 수정 API
     * PUT /api/auth/profile
     * 
     * @param request 프로필 수정 요청 정보
     * @return 수정된 사용자 정보
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<SignupResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        String userId = SecurityContextHolder.getContext().getAuthentication().getName();
        SignupResponse response = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.success(response, "프로필 수정이 완료되었습니다."));
    }
}