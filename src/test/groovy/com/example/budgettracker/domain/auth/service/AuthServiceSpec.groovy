package com.example.budgettracker.domain.auth.service

import com.example.budgettracker.domain.auth.dto.LoginRequest
import com.example.budgettracker.domain.auth.dto.LoginResponse
import com.example.budgettracker.domain.user.User
import com.example.budgettracker.domain.user.repository.UserRepository
import com.example.budgettracker.global.exception.BusinessException
import com.example.budgettracker.global.util.AESUtil
import com.example.budgettracker.global.util.JwtUtil
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification
import spock.lang.Subject

class AuthServiceSpec extends Specification {

    UserRepository userRepository = Mock()
    PasswordEncoder passwordEncoder = Mock()
    JwtUtil jwtUtil = Mock()
    AESUtil aesUtil = Mock()

    @Subject
    AuthService authService = new AuthService(userRepository, passwordEncoder, jwtUtil, aesUtil)

    def "로그인 성공 시 JWT 토큰을 반환한다"() {
        given:
        def email = "test@example.com"
        def password = "password123"
        def userId = "user123"
        def token = "jwt.token.here"

        def loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build()

        def user = User.builder()
                .id(userId)
                .email(email)
                .password("encodedPassword")
                .build()

        when:
        def result = authService.login(loginRequest)

        then:
        1 * userRepository.findByEmail(email) >> Optional.of(user)
        1 * passwordEncoder.matches(password, user.getPassword()) >> true
        1 * jwtUtil.generateToken(userId) >> token

        result instanceof LoginResponse
        result.getToken() == token
    }

    def "존재하지 않는 이메일로 로그인 시도 시 예외가 발생한다"() {
        given:
        def email = "nonexistent@example.com"
        def password = "password123"

        def loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build()

        when:
        authService.login(loginRequest)

        then:
        1 * userRepository.findByEmail(email) >> Optional.empty()
        0 * passwordEncoder.matches(_, _)
        0 * jwtUtil.generateToken(_)

        thrown(BusinessException)
    }

    def "잘못된 비밀번호로 로그인 시도 시 예외가 발생한다"() {
        given:
        def email = "test@example.com"
        def password = "wrongpassword"
        def userId = "user123"

        def loginRequest = LoginRequest.builder()
                .email(email)
                .password(password)
                .build()

        def user = User.builder()
                .id(userId)
                .email(email)
                .password("encodedPassword")
                .build()

        when:
        authService.login(loginRequest)

        then:
        1 * userRepository.findByEmail(email) >> Optional.of(user)
        1 * passwordEncoder.matches(password, user.getPassword()) >> false
        0 * jwtUtil.generateToken(_)

        thrown(BusinessException)
    }
} 