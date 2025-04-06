package com.example.budgettracker.domain.statistics.controller;

import com.example.budgettracker.domain.statistics.dto.BudgetComparisonResponse;
import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
import com.example.budgettracker.domain.statistics.dto.PeriodTrendResponse;
import com.example.budgettracker.domain.statistics.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.YearMonth;

/**
 * 통계 및 분석 관련 API를 제공하는 컨트롤러
 * 
 * @RestController: REST API 컨트롤러임을 명시
 * @RequestMapping: 기본 경로 설정
 * @Tag: Swagger 문서용 태그
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@RestController
@RequestMapping("/api/statistics")
@Tag(name = "Statistics", description = "통계 및 분석 API")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class StatisticsController {

    private final StatisticsService statisticsService;

    /**
     * 월별 통계 정보를 조회하는 API
     * 
     * @param authentication 인증 정보
     * @param yearMonth 조회할 년월 (yyyy-MM 형식)
     * @return 월별 통계 응답
     */
    @Operation(summary = "월별 통계 조회", description = "특정 월의 수입, 지출, 순수익 및 카테고리별 지출 통계를 조회합니다.")
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyStatisticsResponse> getMonthlyStatistics(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "조회할 년월 (yyyy-MM 형식)", example = "2024-03")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        String userId = authentication.getName();
        MonthlyStatisticsResponse response = statisticsService.getMonthlyStatistics(
            userId, yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 기간별 수입/지출 추이를 조회하는 API
     * 
     * @param authentication 인증 정보
     * @param startYearMonth 시작 년월 (yyyy-MM 형식)
     * @param endYearMonth 종료 년월 (yyyy-MM 형식)
     * @return 기간별 추이 응답
     */
    @Operation(summary = "기간별 추이 조회", description = "특정 기간 동안의 수입, 지출, 순수익 추이를 조회합니다.")
    @GetMapping("/trend")
    public ResponseEntity<PeriodTrendResponse> getPeriodTrend(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "시작 년월 (yyyy-MM 형식)", example = "2024-01")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startYearMonth,
            @Parameter(description = "종료 년월 (yyyy-MM 형식)", example = "2024-03")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endYearMonth) {
        String userId = authentication.getName();
        PeriodTrendResponse response = statisticsService.getPeriodTrend(
            userId,
            startYearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")),
            endYearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        return ResponseEntity.ok(response);
    }
    
    /**
     * 예산 대비 지출 통계를 조회하는 API
     * 
     * @param authentication 인증 정보
     * @param yearMonth 조회할 년월 (yyyy-MM 형식)
     * @return 예산 대비 지출 응답
     */
    @Operation(summary = "예산 대비 지출 조회", description = "특정 월의 예산 대비 실제 지출 통계를 조회합니다.")
    @GetMapping("/budget-comparison")
    public ResponseEntity<BudgetComparisonResponse> getBudgetComparison(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "조회할 년월 (yyyy-MM 형식)", example = "2024-03")
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth yearMonth) {
        String userId = authentication.getName();
        BudgetComparisonResponse response = statisticsService.getBudgetComparison(
            userId, yearMonth.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM")));
        return ResponseEntity.ok(response);
    }
} 