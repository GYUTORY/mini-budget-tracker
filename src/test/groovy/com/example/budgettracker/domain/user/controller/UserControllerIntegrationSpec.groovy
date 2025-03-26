package com.example.budgettracker.domain.user.controller

import com.example.budgettracker.domain.user.dto.SignupRequest
import com.example.budgettracker.domain.user.dto.SignupResponse
import com.example.budgettracker.domain.user.repository.UserRepository
import com.example.budgettracker.domain.user.service.UserService
import com.example.budgettracker.global.config.SecurityConfig
import com.example.budgettracker.global.exception.GlobalExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.mockito.ArgumentMatchers.any
import static org.mockito.BDDMockito.given
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * UserController의 회원가입 API에 대한 통합 테스트 클래스
 * 
 * @WebMvcTest: Spring MVC 테스트를 위한 어노테이션
 * - 웹 계층의 빈들만 로드하여 가벼운 테스트 환경 구성
 * - MockMvc를 자동으로 구성
 * 
 * @ContextConfiguration: 테스트에 필요한 설정 클래스들을 지정
 * - SecurityConfig를 테스트 환경에 적용
 */
@WebMvcTest
@ContextConfiguration(classes = [UserController, SecurityConfig, GlobalExceptionHandler])
class UserControllerIntegrationSpec extends Specification {

    // @Autowired: Spring의 의존성 주입 어노테이션
    // - 테스트에 필요한 빈들을 자동으로 주입
    @Autowired
    MockMvc mockMvc  // HTTP 요청을 시뮬레이션하는 테스트 도구

    @Autowired
    ObjectMapper objectMapper  // JSON 직렬화/역직렬화를 담당하는 Jackson 라이브러리

    // @MockBean: Spring의 Mock 객체 생성 어노테이션
    // - 실제 객체 대신 테스트용 가짜 객체를 생성하여 주입
    @MockBean
    UserService userService  // 실제 서비스 대신 Mock 객체 사용

    @MockBean
    UserRepository userRepository  // 실제 리포지토리 대신 Mock 객체 사용

    /**
     * @WithMockUser: Spring Security 테스트를 위한 어노테이션
     * - 테스트 실행 시 인증된 사용자로 시뮬레이션
     */
    @WithMockUser
    def "회원가입 API 테스트"() {
        given: "테스트 데이터 준비"
        // 회원가입 요청 DTO 생성
        def request = new SignupRequest(
                email: "test@example.com",
                password: "password123",
                name: "테스트유저"
        )
        // 예상되는 응답 DTO 생성
        def response = SignupResponse.builder()
                .id(1L)
                .email(request.getEmail())
                .name(request.getName())
                .build()
        // Mock 객체의 동작 정의
        given(userService.signup(any(SignupRequest.class))).willReturn(response)

        when: "API 요청 실행"
        // POST /api/auth/signup 요청 수행
        def result = mockMvc.perform(
                post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )

        then: "응답 검증"
        // HTTP 상태 코드 및 응답 본문 검증
        result.andExpect(status().isCreated())
                .andExpect(jsonPath('$.result').value(true))
                .andExpect(jsonPath('$.code').value("200"))
                .andExpect(jsonPath('$.resultSet.id').exists())
                .andExpect(jsonPath('$.resultSet.email').value(request.getEmail()))
                .andExpect(jsonPath('$.resultSet.name').value(request.getName()))
                .andExpect(jsonPath('$.msg').value("회원가입이 완료되었습니다."))
    }

    @WithMockUser
    def "이메일 형식이 잘못된 경우 회원가입 실패"() {
        given: "잘못된 이메일 형식의 요청 데이터 준비"
        def request = new SignupRequest(
                email: "invalid-email",  // '@' 없는 잘못된 이메일 형식
                password: "password123",
                name: "테스트유저"
        )

        when: "API 요청 실행"
        def result = mockMvc.perform(
                post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )

        then: "응답 검증"
        // 400 Bad Request 응답 및 에러 메시지 검증
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath('$.result').value(false))
                .andExpect(jsonPath('$.code').value("400"))
                .andExpect(jsonPath('$.msg').value("유효한 이메일 형식이 아닙니다."))
    }

    @WithMockUser
    def "이메일 중복 검사 API 테스트"() {
        given: "테스트 데이터 준비"
        def email = "test@example.com"
        // Mock 객체의 동작 정의
        given(userService.isEmailAvailable(email)).willReturn(true)

        when: "API 요청 실행"
        def result = mockMvc.perform(
                get("/api/auth/check-email")
                        .param("email", email)
        )

        then: "응답 검증"
        // 200 OK 응답 및 사용 가능한 이메일 메시지 검증
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.result').value(true))
                .andExpect(jsonPath('$.code').value("200"))
                .andExpect(jsonPath('$.resultSet').value(true))
                .andExpect(jsonPath('$.msg').value("사용 가능한 이메일입니다."))

        when: "가입된 이메일로 중복 검사"
        given(userService.isEmailAvailable(email)).willReturn(false)
        result = mockMvc.perform(
                get("/api/auth/check-email")
                        .param("email", email)
        )

        then: "응답 검증"
        // 이미 사용 중인 이메일 응답 검증
        result.andExpect(status().isOk())
                .andExpect(jsonPath('$.result').value(true))
                .andExpect(jsonPath('$.code').value("200"))
                .andExpect(jsonPath('$.resultSet').value(false))
                .andExpect(jsonPath('$.msg').value("이미 사용 중인 이메일입니다."))
    }
}