package com.example.budgettracker.domain.budget.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 예산 관련 API를 제공하는 컨트롤러
 * 
 * 주요 기능:
 * - 예산 생성 (/api/budgets)
 * - 예산 조회 (/api/budgets/{id})
 * - 예산 목록 조회 (/api/budgets)
 * - 예산 수정 (/api/budgets/{id})
 * - 예산 삭제 (/api/budgets/{id})
 * 
 * 보안:
 * - 모든 엔드포인트는 인증 필요
 * - JWT 토큰 기반 인증 사용
 * - 입력값 검증 (@Valid) 적용
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "예산", description = "예산 관련 API")
@RestController
@RequestMapping("/api/budgets")
@RequiredArgsConstructor
public class BudgetController {
// ... existing code ...
} 