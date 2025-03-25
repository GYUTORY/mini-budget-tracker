package com.example.budgettracker.domain.user.controller;

import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.service.UserService;
import com.example.budgettracker.global.dto.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 인증 관련 API를 제공하는 컨트롤러
 */
@RestController // 이 클래스가 REST API 컨트롤러임을 명시 (JSON 반환)
@RequestMapping("/api/auth") // 이 컨트롤러의 공통 URL Prefix 지정
@RequiredArgsConstructor // Lombok: final 필드를 자동으로 생성자 주입
public class UserController {

    private final UserService userService; // 회원가입 로직을 처리할 서비스 클래스 주입

    @PostMapping("/check-email")
    public ResponseEntity<ApiResponse<Boolean>> checkEmail(@RequestBody String email) {
        boolean isAvailable = userService.isEmailAvailable(email);
        String message = isAvailable ? "사용 가능한 이메일입니다." : "이미 사용 중인 이메일입니다.";
        return ResponseEntity.ok(ApiResponse.success(isAvailable, message));
    }

    /**
     * 회원가입 API
     * POST /api/auth/signup
     *
     * @param request 클라이언트가 보낸 회원가입 요청 DTO
     * @return 회원가입 결과 DTO와 201(CREATED) 상태 코드
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest request) {
        // @Valid: DTO에 선언된 유효성 검증 어노테이션을 실행함
        // @RequestBody: HTTP 요청 본문(JSON)을 Java 객체로 매핑

        SignupResponse response = userService.signup(request); // 실제 가입 처리
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "회원가입이 완료되었습니다."));
    }
}