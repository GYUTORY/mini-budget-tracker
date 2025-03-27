package com.example.budgettracker.domain.user.controller

import com.example.budgettracker.domain.user.dto.SignupRequest
import com.example.budgettracker.domain.user.dto.SignupResponse
import com.example.budgettracker.domain.user.service.UserService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(UserController)
class UserControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @MockBean
    private UserService userService

    def "회원가입 성공"() {
        given:
        def request = new SignupRequest(
            email: "test@example.com",
            password: "password123",
            name: "홍길동"
        )
        def response = new SignupResponse(
            id: "test-user-id",
            email: request.getEmail(),
            name: request.getName()
        )
        
        and:
        userService.signup(request) >> response

        when:
        def result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath("$.success").value(true))
        result.andExpect(jsonPath("$.data.email").value(request.getEmail()))
        result.andExpect(jsonPath("$.data.name").value(request.getName()))
    }

    def "회원가입 실패 - 잘못된 요청"() {
        given:
        def request = new SignupRequest(
            email: "invalid-email",
            password: "",  // 빈 비밀번호
            name: ""  // 빈 이름
        )

        when:
        def result = mockMvc.perform(post("/api/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isBadRequest())
    }

    @WithMockUser
    def "마이페이지 조회 성공"() {
        given:
        def userId = "test-user-id"
        def response = new SignupResponse(
            id: userId,
            email: "test@example.com",
            name: "홍길동"
        )
        
        and:
        userService.getUserInfo(userId) >> response

        when:
        def result = mockMvc.perform(get("/api/users/me"))

        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath("$.success").value(true))
        result.andExpect(jsonPath("$.data.email").value(response.getEmail()))
        result.andExpect(jsonPath("$.data.name").value(response.getName()))
    }

    def "마이페이지 조회 실패 - 인증되지 않은 사용자"() {
        when:
        def result = mockMvc.perform(get("/api/users/me"))

        then:
        result.andExpect(status().isUnauthorized())
    }
} 