package com.example.budgettracker.domain.auth.service;

import com.example.budgettracker.domain.auth.dto.LoginRequest;
import com.example.budgettracker.domain.auth.dto.LoginResponse;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.BusinessException;
import com.example.budgettracker.global.util.AESUtil;
import com.example.budgettracker.global.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증 관련 비즈니스 로직을 처리하는 서비스
 * 로그인, 비밀번호 검증, JWT 토큰 생성 등의 기능을 제공합니다.
 */
@Service // 스프링 서비스 컴포넌트임을 명시
@RequiredArgsConstructor // final 필드에 대한 생성자 자동 생성
@Transactional(readOnly = true) // 트랜잭션 설정 (기본적으로 읽기 전용)
public class AuthService {

    private final UserRepository userRepository; // 사용자 정보 관련 데이터 접근 객체
    private final PasswordEncoder passwordEncoder; // 비밀번호 암호화/검증을 위한 인코더
    private final JwtUtil jwtUtil; // JWT 토큰 생성 및 검증 유틸리티
    private final AESUtil aesUtil; // AES 암호화/복호화 유틸리티

    /**
     * 사용자 로그인을 처리하는 메서드
     * 
     * @param request 로그인 요청 정보 (이메일, 비밀번호)
     * @return JWT 토큰이 포함된 로그인 응답
     * @throws BusinessException 이메일이 존재하지 않거나 비밀번호가 일치하지 않는 경우
     */
    public LoginResponse login(LoginRequest request) {
        // 1. 이메일로 사용자 조회
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String token = jwtUtil.generateToken(user.getId());
        
        // 4. 로그인 응답 생성 및 반환
        return LoginResponse.builder()
                .token(token)
                .build();
    }
} 