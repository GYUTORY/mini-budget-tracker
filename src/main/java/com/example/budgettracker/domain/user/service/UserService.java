package com.example.budgettracker.domain.user.service;

import com.example.budgettracker.domain.user.User;
import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.dto.UpdateProfileRequest;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.error.BusinessException;
import com.example.budgettracker.global.error.ErrorCode;
import com.example.budgettracker.global.util.AESUtil;
import com.example.budgettracker.global.util.JwtUtil;
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
    private final JwtUtil jwtUtil;
    private final AESUtil aesUtil;

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public SignupResponse signup(SignupRequest request) {
        if (existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(aesUtil.encrypt(request.getName()))
                .build();

        userRepository.save(user);
        return SignupResponse.from(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getId());
        return LoginResponse.builder()
                .token(token)
                .build();
    }

    public void logout(String userId) {
        // JWT 토큰은 클라이언트 측에서 삭제하므로 서버 측에서는 특별한 처리가 필요하지 않습니다.
    }

    @Transactional
    public SignupResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (request.getName() != null) {
            user.updateName(aesUtil.encrypt(request.getName()));
        }

        if (request.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        return SignupResponse.from(user);
    }

    public SignupResponse getUserInfo(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return SignupResponse.from(user);
    }
}