package com.example.budgettracker.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

/**
 * Swagger(OpenAPI) 설정 클래스
 * 
 * @Configuration: 설정 클래스임을 나타냄
 */
@Configuration
public class SwaggerConfig {

    /**
     * OpenAPI 설정
     * 
     * @return OpenAPI 객체
     */
    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("예산 관리 API")
                .version("v1.0")
                .description("예산 관리 애플리케이션의 REST API 문서입니다.\n\n" +
                        "## 인증\n" +
                        "- 대부분의 API는 JWT 토큰 인증이 필요합니다.\n" +
                        "- 회원가입과 로그인 API는 인증이 필요하지 않습니다.\n\n" +
                        "## 에러 코드\n" +
                        "- 400: 잘못된 요청\n" +
                        "- 401: 인증 실패\n" +
                        "- 403: 권한 없음\n" +
                        "- 404: 리소스를 찾을 수 없음\n" +
                        "- 500: 서버 내부 오류")
                .contact(new Contact()
                        .name("김경윤")
                        .email("gyutory@gmail.com"))
                .license(new License()
                        .name("Apache 2.0")
                        .url("http://www.apache.org/licenses/LICENSE-2.0.html"));

        Server server = new Server()
                .url("/")
                .description("로컬 서버");

        // JWT 보안 스키마 설정
        SecurityScheme securityScheme = new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("JWT 토큰을 입력해주세요. (Bearer 접두사는 자동으로 추가됩니다.)");

        // API 태그 설정
        List<Tag> tags = Arrays.asList(
                new Tag().name("인증").description("회원가입, 로그인 등 인증 관련 API"),
                new Tag().name("사용자").description("사용자 정보 관리 API"),
                new Tag().name("거래 내역").description("거래 내역 관리 API")
        );

        // 보안 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList("bearer-key");

        return new OpenAPI()
                .info(info)
                .servers(List.of(server))
                .components(new Components().addSecuritySchemes("bearer-key", securityScheme))
                .security(List.of(securityRequirement))
                .tags(tags);
    }
} 