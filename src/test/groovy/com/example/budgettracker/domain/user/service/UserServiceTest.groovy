package com.example.budgettracker.domain.user.service

import com.example.budgettracker.domain.user.dto.SignupRequest
import com.example.budgettracker.domain.user.dto.SignupResponse
import com.example.budgettracker.domain.user.entity.User
import com.example.budgettracker.domain.user.repository.UserRepository
import com.example.budgettracker.global.exception.BusinessException
import com.example.budgettracker.global.util.AESUtil
import spock.lang.Specification
import spock.lang.Subject

class UserServiceTest extends Specification {

    UserRepository userRepository = Mock()
    AESUtil aesUtil = Mock()

    @Subject
    UserService userService = new UserService(userRepository, aesUtil)

    def "회원가입 성공"() {
        given:
        def request = new SignupRequest(
            email: "test@example.com",
            password: "password123",
            name: "홍길동"
        )
        def encryptedName = "encryptedName"
        def userId = "test-user-id"
        
        and:
        aesUtil.encrypt(request.getName()) >> encryptedName
        userRepository.save(_ as User) >> new User(
            id: userId,
            email: request.getEmail(),
            password: request.getPassword(),
            name: encryptedName
        )

        when:
        def result = userService.signup(request)

        then:
        result != null
        result.getEmail() == request.getEmail()
        result.getName() == request.getName()
        1 * userRepository.save(_ as User)
        1 * aesUtil.encrypt(request.getName())
    }

    def "회원가입 실패 - 이메일 중복"() {
        given:
        def request = new SignupRequest(
            email: "test@example.com",
            password: "password123",
            name: "홍길동"
        )
        
        and:
        userRepository.existsByEmail(request.getEmail()) >> true

        when:
        userService.signup(request)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "이미 사용 중인 이메일입니다."
    }

    def "회원 정보 조회 성공"() {
        given:
        def userId = "test-user-id"
        def encryptedName = "encryptedName"
        def decryptedName = "홍길동"
        
        and:
        def user = new User(
            id: userId,
            email: "test@example.com",
            password: "password123",
            name: encryptedName
        )
        userRepository.findById(userId) >> Optional.of(user)
        aesUtil.decrypt(encryptedName) >> decryptedName

        when:
        def result = userService.getUserInfo(userId)

        then:
        result != null
        result.getEmail() == user.getEmail()
        result.getName() == decryptedName
        1 * userRepository.findById(userId)
        1 * aesUtil.decrypt(encryptedName)
    }

    def "회원 정보 조회 실패 - 사용자 없음"() {
        given:
        def userId = "test-user-id"
        
        and:
        userRepository.findById(userId) >> Optional.empty()

        when:
        userService.getUserInfo(userId)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "사용자를 찾을 수 없습니다."
    }
} 