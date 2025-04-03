package com.example.budgettracker.domain.statistics.controller;

import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
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

@Tag(name = "통계", description = "통계 및 분석 API")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "월별 통계 조회", description = "특정 월의 수입/지출 통계를 조회합니다.")
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
} 