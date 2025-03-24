package com.example.budgettracker.domain.user.service;

import com.example.budgettracker.domain.user.dto.SignupRequest;
import com.example.budgettracker.domain.user.dto.SignupResponse;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 관련 비즈니스 로직을 담당하는 서비스 클래스
 */
@Service // Spring이 이 클래스를 서비스 컴포넌트로 인식하여 빈으로 등록
@RequiredArgsConstructor // final 필드를 자동으로 생성자 주입해주는 Lombok 어노테이션
public class UserService {

    // DI 대상: UserRepository를 통해 DB 작업 수행
    private final UserRepository userRepository;

    // DI 대상: 비밀번호 암호화를 위한 Spring Security의 PasswordEncoder
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 기능
     * @param request - 회원가입 요청 정보 (이메일, 비밀번호, 이름)
     * @return 회원가입 결과 정보 (id, 이메일, 이름)
     */
    @Transactional // 이 메서드 안의 DB 작업을 하나의 트랜잭션으로 묶음
    public SignupResponse signup(SignupRequest request) {
        // [1] 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            // 이미 존재하면 예외 발생
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // [2] 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // [3] User 엔티티 생성 (Builder 패턴 사용)
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .build();

        // [4] DB에 저장
        User savedUser = userRepository.save(user);

        // [5] 응답 DTO 생성 (DB에 저장된 유저 정보 사용)
        return SignupResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }
}