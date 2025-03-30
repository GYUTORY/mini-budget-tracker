package com.example.budgettracker.domain.user.service;

import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.dto.LoginRequest;
import com.example.budgettracker.domain.user.dto.LoginResponse;
import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.dto.UpdateProfileRequest;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.CustomException;
import com.example.budgettracker.global.exception.ErrorCode;
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

    @Transactional
    public User signup(SignupRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 이메일입니다.");
        }

        String encryptedName = aesUtil.encrypt(request.getName());
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(encryptedName)
                .build();

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return LoginResponse.builder()
                .token(token)
                .message("로그인이 완료되었습니다.")
                .build();
    }

    public void logout(String userId) {
        // JWT 토큰은 클라이언트 측에서 삭제하므로 서버 측에서는 특별한 처리가 필요하지 않습니다.
    }

    @Transactional
    public SignupResponse updateProfile(String userId, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getName() != null) {
            user.updateName(aesUtil.encrypt(request.getName()));
        }

        if (request.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        return SignupResponse.of(user, "프로필이 수정되었습니다.");
    }

    @Transactional
    public void updateUserInfo(String userId, SignupRequest request) {
        User user = userRepository.findByEmail(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if (request.getName() != null) {
            user.updateName(aesUtil.encrypt(request.getName()));
        }

        if (request.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }
    }

    @Transactional(readOnly = true)
    public User getProfile(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}