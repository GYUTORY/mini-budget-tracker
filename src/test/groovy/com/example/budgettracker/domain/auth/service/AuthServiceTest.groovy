package com.example.budgettracker.domain.auth.service

import com.example.budgettracker.domain.auth.dto.LoginRequest
import com.example.budgettracker.domain.auth.dto.LoginResponse
import com.example.budgettracker.domain.user.entity.User
import com.example.budgettracker.domain.user.repository.UserRepository
import com.example.budgettracker.global.exception.BusinessException
import com.example.budgettracker.global.util.JwtUtil
import spock.lang.Specification
import spock.lang.Subject

import java.util.Optional

class AuthServiceTest extends Specification {

    UserRepository userRepository = Mock()
    JwtUtil jwtUtil = Mock()

    @Subject
    AuthService authService = new AuthService(userRepository, jwtUtil)

    def "로그인 성공"() {
        given:
        def request = new LoginRequest(
            email: "test@example.com",
            password: "password123"
        )
        def userId = "test-user-id"
        def token = "test.jwt.token"
        
        and:
        def user = new User(
            id: userId,
            email: request.getEmail(),
            password: request.getPassword()
        )
        userRepository.findByEmail(request.getEmail()) >> Optional.of(user)
        jwtUtil.generateToken(userId) >> token

        when:
        def result = authService.login(request)

        then:
        result != null
        result.getToken() == token
        1 * userRepository.findByEmail(request.getEmail())
        1 * jwtUtil.generateToken(userId)
    }

    def "로그인 실패 - 이메일 없음"() {
        given:
        def request = new LoginRequest(
            email: "test@example.com",
            password: "password123"
        )
        
        and:
        userRepository.findByEmail(request.getEmail()) >> Optional.empty()

        when:
        authService.login(request)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "이메일 또는 비밀번호가 일치하지 않습니다."
    }

    def "로그인 실패 - 비밀번호 불일치"() {
        given:
        def request = new LoginRequest(
            email: "test@example.com",
            password: "wrong-password"
        )
        
        and:
        def user = new User(
            id: "test-user-id",
            email: request.getEmail(),
            password: "correct-password"
        )
        userRepository.findByEmail(request.getEmail()) >> Optional.of(user)

        when:
        authService.login(request)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "이메일 또는 비밀번호가 일치하지 않습니다."
    }
} 