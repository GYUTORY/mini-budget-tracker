package com.example.budgettracker.domain.auth.controller

import com.example.budgettracker.domain.auth.dto.LoginRequest
import com.example.budgettracker.domain.auth.dto.LoginResponse
import com.example.budgettracker.domain.auth.service.AuthService
import com.example.budgettracker.global.exception.BusinessException
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(AuthController)
class AuthControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @MockBean
    private AuthService authService

    def "로그인 성공"() {
        given:
        def request = new LoginRequest(
            email: "test@example.com",
            password: "password123"
        )
        def response = new LoginResponse(
            token: "test.jwt.token"
        )
        
        and:
        authService.login(request) >> response

        when:
        def result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath("$.success").value(true))
        result.andExpect(jsonPath("$.data.token").value(response.getToken()))
    }

    def "로그인 실패 - 잘못된 요청"() {
        given:
        def request = new LoginRequest(
            email: "invalid-email",
            password: ""  // 빈 비밀번호
        )

        when:
        def result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isBadRequest())
    }

    def "로그인 실패 - 인증 실패"() {
        given:
        def request = new LoginRequest(
            email: "test@example.com",
            password: "wrong-password"
        )
        
        and:
        authService.login(request) >> { throw new BusinessException("이메일 또는 비밀번호가 일치하지 않습니다.") }

        when:
        def result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isUnauthorized())
        result.andExpect(jsonPath("$.success").value(false))
        result.andExpect(jsonPath("$.message").value("이메일 또는 비밀번호가 일치하지 않습니다."))
    }
} 