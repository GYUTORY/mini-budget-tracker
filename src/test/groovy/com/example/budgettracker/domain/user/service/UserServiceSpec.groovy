package com.example.budgettracker.domain.user.service

import com.example.budgettracker.domain.user.dto.SignupRequest
import com.example.budgettracker.domain.user.entity.User
import com.example.budgettracker.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import spock.lang.Specification

/**
 * UserService의 회원가입 기능에 대한 단위 테스트 클래스
 */
class UserServiceSpec extends Specification {

    // 의존성들을 테스트용으로 Mock 객체로 생성
    UserRepository userRepository
    PasswordEncoder passwordEncoder
    UserService userService

    /**
     * 각 테스트 전에 실행되는 setup() 메서드
     * 여기서 Mock 객체들을 초기화하고 UserService에 주입함
     */
    def setup() {
        userRepository = Mock(UserRepository)         // Repository Mock
        passwordEncoder = Mock(PasswordEncoder)       // PasswordEncoder Mock
        userService = new UserService(userRepository, passwordEncoder)
    }

    /**
     * [정상 흐름] 회원가입 요청이 들어왔을 때:
     * - 이메일이 중복되지 않고
     * - 비밀번호가 암호화되며
     * - 유저가 저장되고
     * - 정상 응답이 리턴되는지 확인
     */
    def "회원가입 성공 테스트"() {
        given: "회원가입 요청이 주어지고, 저장될 유저 객체가 준비됨"
        def request = new SignupRequest(
                email: "test@example.com",
                password: "password123",
                name: "테스트유저"
        )
        def encodedPassword = "encodedPassword123" // 암호화된 비밀번호 가정
        def savedUser = User.builder()
                .id(1L)
                .email(request.getEmail())
                .password(encodedPassword)
                .name(request.getName())
                .build()

        when: "회원가입 메서드 호출"
        def response = userService.signup(request)

        then: "다음 메서드들이 호출되고, 그 결과를 반환함"
        1 * userRepository.existsByEmail(request.getEmail()) >> false   // 이메일 중복 체크 → false
        1 * passwordEncoder.encode(request.getPassword()) >> encodedPassword // 비밀번호 암호화
        1 * userRepository.save(_) >> savedUser                         // 저장 후 저장된 유저 반환

        and: "응답값이 예상한 유저 정보와 일치하는지 검증"
        response.getId() == savedUser.getId()
        response.getEmail() == savedUser.getEmail()
        response.getName() == savedUser.getName()
    }

    /**
     * [예외 흐름] 이미 존재하는 이메일로 회원가입 시 IllegalArgumentException 발생 확인
     */
    def "이미 존재하는 이메일로 회원가입 시 예외 발생"() {
        given: "이미 존재하는 이메일을 가진 회원가입 요청"
        def request = new SignupRequest(
                email: "exists@example.com",
                password: "password123",
                name: "테스트유저"
        )

        when: "회원가입 메서드 호출"
        userService.signup(request)

        then: "이메일 중복 확인 후 예외 발생"
        1 * userRepository.existsByEmail(request.getEmail()) >> true // 이미 존재하는 이메일임을 반환
        thrown(IllegalArgumentException) // 예외 발생 확인
    }
}