package com.example.budgettracker.domain.user.service;

import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.dto.UpdateProfileRequest;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.BusinessException;
import com.example.budgettracker.global.security.JwtTokenProvider;
import com.example.budgettracker.global.util.AESUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil;

    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // 이메일 중복 체크
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException("이미 사용 중인 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());
        // 이름 암호화
        String encryptedName = aesUtil.encrypt(request.getName());

        // 사용자 엔티티 생성 및 저장
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(encryptedName)
                .build();

        userRepository.save(user);

        // 응답 생성
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(aesUtil.decrypt(user.getName()))
                .build();
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        // 이메일로 사용자 찾기
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 비밀번호 확인
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성
        String token = jwtTokenProvider.createToken(user.getId());

        // 응답 생성
        return LoginResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .name(aesUtil.decrypt(user.getName()))
                .build();
    }

    @Transactional
    public void logout(String userId) {
        // 현재 사용자의 토큰을 무효화
        jwtTokenProvider.invalidateToken(userId);
    }

    public SignupResponse getUserInfo(String userId) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));

        // 응답 생성
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(aesUtil.decrypt(user.getName()))
                .build();
    }

    @Transactional
    public SignupResponse updateProfile(String userId, UpdateProfileRequest request) {
        // 사용자 찾기
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException("사용자를 찾을 수 없습니다."));

        // 이름 암호화
        String encryptedName = aesUtil.encrypt(request.getName());
        
        // 사용자 정보 업데이트
        user.updateName(encryptedName);

        // 응답 생성
        return SignupResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(aesUtil.decrypt(user.getName()))
                .build();
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}