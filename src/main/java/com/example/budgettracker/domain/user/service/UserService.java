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
 * 
 * @Service: Spring이 이 클래스를 서비스 컴포넌트로 인식하여 빈으로 등록
 * - 비즈니스 로직을 담당하는 계층임을 명시
 * - 컴포넌트 스캔의 대상이 됨
 * 
 * @RequiredArgsConstructor: Lombok이 제공하는 어노테이션
 * - final 필드에 대한 생성자를 자동으로 생성
 * - 의존성 주입을 위한 생성자 주입 방식 사용
 */
@Service
@RequiredArgsConstructor
public class UserService {

    // UserRepository를 통한 데이터 접근
    // - JPA를 사용한 데이터베이스 작업 수행
    // - final로 선언하여 불변성 보장
    private final UserRepository userRepository;

    // 비밀번호 암호화를 위한 PasswordEncoder
    // - Spring Security에서 제공하는 비밀번호 암호화 도구
    // - BCrypt 알고리즘을 사용하여 비밀번호를 안전하게 저장
    private final PasswordEncoder passwordEncoder;

    /**
     * 이메일 사용 가능 여부를 확인합니다.
     *
     * @param email 확인할 이메일
     * @return 이메일 사용 가능 여부
     */
    public boolean isEmailAvailable(String email) {
        return !userRepository.existsByEmail(email);
    }

    /**
     * 회원가입 기능을 수행하는 메서드
     * 
     * @Transactional: 트랜잭션 관리
     * - 메서드 내의 모든 데이터베이스 작업을 하나의 트랜잭션으로 처리
     * - 롤백 가능한 작업 단위로 구성
     * 
     * @param request: 회원가입 요청 정보를 담은 DTO
     * - 이메일, 비밀번호, 이름 등의 정보 포함
     * - @Valid 어노테이션으로 유효성 검증
     * 
     * @return SignupResponse: 회원가입 결과 정보
     * - 생성된 사용자의 ID, 이메일, 이름 등 포함
     * 
     * @throws IllegalArgumentException: 이메일 중복 시 발생
     */
    @Transactional
    public SignupResponse signup(SignupRequest request) {
        // [1] 이메일 중복 체크
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }

        // [2] 비밀번호 암호화
        // - 평문 비밀번호를 BCrypt로 암호화하여 저장
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // [3] User 엔티티 생성
        // - Builder 패턴을 사용하여 객체 생성
        // - 불변성을 보장하는 객체 생성 방식
        User user = User.builder()
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .build();

        // [4] 데이터베이스에 저장
        // - JPA를 통해 엔티티를 영구 저장소에 저장
        // - 저장된 엔티티는 영속 상태가 됨
        User savedUser = userRepository.save(user);

        // [5] 응답 DTO 생성
        // - 저장된 엔티티의 정보를 DTO로 변환
        // - 클라이언트에 필요한 정보만 포함
        return SignupResponse.builder()
                .id(savedUser.getId())
                .email(savedUser.getEmail())
                .name(savedUser.getName())
                .build();
    }
}