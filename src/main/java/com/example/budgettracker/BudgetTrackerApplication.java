package com.example.budgettracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 예산 관리 애플리케이션의 메인 클래스
 * 
 * @SpringBootApplication: Spring Boot 애플리케이션의 핵심 어노테이션
 *   - @Configuration: 클래스가 설정 클래스임을 나타냄
 *   - @EnableAutoConfiguration: Spring Boot의 자동 설정을 활성화
 *   - @ComponentScan: 현재 패키지와 하위 패키지에서 컴포넌트를 스캔
 * 
 * @EnableJpaAuditing: JPA Auditing 기능 활성화
 *   - 생성일시, 수정일시 등의 자동 기록을 위한 설정
 */
@SpringBootApplication
@EnableJpaAuditing
public class BudgetTrackerApplication {

    /**
     * 애플리케이션의 진입점
     * 
     * @param args 커맨드 라인 인자
     */
    public static void main(String[] args) {
        SpringApplication.run(BudgetTrackerApplication.class, args);
    }
} 