package com.example.budgettracker.domain.budget.controller;

import com.example.budgettracker.domain.budget.dto.BudgetRequest;
import com.example.budgettracker.domain.budget.dto.BudgetResponse;
import com.example.budgettracker.domain.budget.service.BudgetService;
import com.example.budgettracker.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    private final BudgetService budgetService;

    @Operation(summary = "예산 생성", description = "새로운 예산을 생성합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createBudget(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetRequest request) {
        Long budgetId = budgetService.createBudget(Long.parseLong(userDetails.getUsername()), request);
        return ResponseEntity.ok(ApiResponse.success(budgetId, "예산이 성공적으로 생성되었습니다."));
    }

    @Operation(summary = "예산 조회", description = "특정 예산을 조회합니다.")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<BudgetResponse>> getBudget(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        BudgetResponse budget = budgetService.getBudget(Long.parseLong(userDetails.getUsername()), id);
        return ResponseEntity.ok(ApiResponse.success(budget, "예산 조회가 완료되었습니다."));
    }

    @Operation(summary = "예산 목록 조회", description = "사용자의 모든 예산을 조회합니다.")
    @SecurityRequirement(name = "bearer-key")
    @GetMapping
    public ResponseEntity<ApiResponse<List<BudgetResponse>>> getBudgets(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails) {
        List<BudgetResponse> budgets = budgetService.getBudgets(Long.parseLong(userDetails.getUsername()));
        return ResponseEntity.ok(ApiResponse.success(budgets, "예산 목록 조회가 완료되었습니다."));
    }

    @Operation(summary = "예산 수정", description = "특정 예산을 수정합니다.")
    @SecurityRequirement(name = "bearer-key")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Long>> updateBudget(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequest request) {
        Long budgetId = budgetService.updateBudget(Long.parseLong(userDetails.getUsername()), id, request);
        return ResponseEntity.ok(ApiResponse.success(budgetId, "예산이 성공적으로 수정되었습니다."));
    }

    @Operation(summary = "예산 삭제", description = "특정 예산을 삭제합니다.")
    @SecurityRequirement(name = "bearer-key")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteBudget(
            @Parameter(hidden = true) @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        budgetService.deleteBudget(Long.parseLong(userDetails.getUsername()), id);
        return ResponseEntity.ok(ApiResponse.success(null, "예산이 성공적으로 삭제되었습니다."));
    }
} 