package com.example.budgettracker.global.security;

import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Spring Security의 UserDetailsService 구현체
 * 사용자 인증을 위한 사용자 정보를 데이터베이스에서 조회
 * 
 * @Service: 서비스 계층의 컴포넌트로 등록
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * 사용자 이메일로 사용자 정보를 조회하여 UserDetails 객체로 변환
     * 
     * @param username 사용자 이메일
     * @return UserDetails 객체
     * @throws UsernameNotFoundException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 사용자 이메일로 사용자 정보 조회
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));

        // UserDetails 객체 생성 및 반환
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 