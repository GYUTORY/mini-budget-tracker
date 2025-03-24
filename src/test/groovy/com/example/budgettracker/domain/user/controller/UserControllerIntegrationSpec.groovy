package com.example.budgettracker.domain.user.controller

import com.example.budgettracker.domain.user.dto.SignupRequest
import com.example.budgettracker.domain.user.repository.UserRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Shared
import spock.lang.Specification

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

/**
 * UserController의 회원가입 API에 대한 통합 테스트 클래스
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // Spring Boot 전체 애플리케이션 컨텍스트를 로드하고 랜덤 포트로 테스트 실행
@AutoConfigureMockMvc // MockMvc를 자동 설정하여 실제 서버를 띄우지 않고 MVC 레이어 테스트 가능하게 함
@ActiveProfiles("test") // 테스트 전용 application-test.yml 프로파일을 적용
@Testcontainers // Testcontainers를 사용하는 테스트임을 선언 (Spock 전용 어노테이션)
class UserControllerIntegrationSpec extends Specification {

    // MySQLContainer는 실제 MySQL을 Docker로 띄우는 Testcontainers 기능
    @Shared // 모든 테스트 메서드 간에 공유되는 정적 객체
    static MySQLContainer mysql = new MySQLContainer("mysql:8.0") // 사용 버전 지정
            .withDatabaseName("testdb")      // 데이터베이스 이름 설정
            .withUsername("testuser")        // DB 접속 사용자 이름
            .withPassword("testpass")        // DB 접속 비밀번호

    /**
     * 정적 DB 연결 정보를 Spring 테스트 환경에 주입하는 메서드
     * @param registry - Spring이 제공하는 프로퍼티 등록기
     */
    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        // docker로 실행되는 MySQL 컨테이너에서 제공하는 접속 정보들을 테스트 환경 변수로 설정
        registry.add("spring.datasource.url", mysql::getJdbcUrl)
        registry.add("spring.datasource.username", mysql::getUsername)
        registry.add("spring.datasource.password", mysql::getPassword)
        registry.add("spring.jpa.hibernate.ddl-auto", { -> "create-drop" }) // 테스트 종료 후 DB 테이블 drop
    }

    @Autowired
    MockMvc mockMvc // 실제 HTTP 요청 없이 Spring MVC 흐름을 테스트할 수 있는 도구

    @Autowired
    ObjectMapper objectMapper // Java 객체 <-> JSON 직렬화/역직렬화를 담당

    @Autowired
    UserRepository userRepository // 테스트 중 실제 DB에 저장된 유저 데이터를 확인할 때 사용

    /**
     * 각 테스트 후, DB를 비워서 테스트 간 데이터 간섭을 방지
     */
    def cleanup() {
        userRepository.deleteAll()
    }

    /**
     * 정상적인 회원가입 요청에 대해 201 Created 상태와 응답 바디 검증
     */
    def "회원가입 API 테스트"() {
        given: "회원가입 요청 객체가 준비됨"
        def request = new SignupRequest(
                email: "test@example.com",
                password: "password123",
                name: "테스트유저"
        )

        when: "회원가입 API 호출"
        def result = mockMvc.perform(
                post("/api/auth/signup") // POST /api/auth/signup 요청
                        .contentType(MediaType.APPLICATION_JSON) // JSON 타입 설정
                        .content(objectMapper.writeValueAsString(request)) // SignupRequest → JSON 문자열 변환 후 전송
        )

        then: "201 상태 코드 및 응답 JSON 검증"
        result.andExpect(status().isCreated()) // HTTP 201 응답 확인
                .andExpect(jsonPath('$.id').exists()) // 응답 JSON에 id 필드 존재
                .andExpect(jsonPath('$.email').value(request.getEmail())) // 응답 email 일치 확인
                .andExpect(jsonPath('$.name').value(request.getName())) // 응답 name 일치 확인

        and: "DB에 해당 이메일의 유저가 존재하는지 검증"
        userRepository.findByEmail(request.getEmail()).isPresent()
    }

    /**
     * 이메일 형식이 잘못된 경우 회원가입이 실패해야 함 (400 Bad Request)
     */
    def "이메일 형식이 잘못된 경우 회원가입 실패"() {
        given: "잘못된 이메일 형식의 요청 준비"
        def request = new SignupRequest(
                email: "invalid-email", // '@' 없는 잘못된 이메일 형식
                password: "password123",
                name: "테스트유저"
        )

        when: "회원가입 API 호출"
        def result = mockMvc.perform(
                post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
        )

        then: "400 상태 코드 응답"
        result.andExpect(status().isBadRequest())
    }
}