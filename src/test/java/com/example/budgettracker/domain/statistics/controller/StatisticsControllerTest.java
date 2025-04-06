package com.example.budgettracker.domain.statistics.controller;

import com.example.budgettracker.domain.statistics.dto.BudgetComparisonResponse;
import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
import com.example.budgettracker.domain.statistics.dto.PeriodTrendResponse;
import com.example.budgettracker.domain.statistics.service.StatisticsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * StatisticsController의 단위 테스트 클래스
 * 
 * @WebMvcTest: Spring MVC 테스트를 위한 어노테이션
 * @Autowired: MockMvc 주입
 * @MockBean: 의존성 주입이 필요한 모의 서비스
 */
@WebMvcTest(StatisticsController.class)
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    private static final String USER_ID = "test-user";
    private static final String YEAR_MONTH = "2024-03";
    private static final String START_YEAR_MONTH = "2024-01";
    private static final String END_YEAR_MONTH = "2024-03";

    /**
     * 월별 통계 조회 API 테스트
     * - HTTP 상태 코드 200 확인
     * - 응답 데이터 구조 및 값 확인
     */
    @Test
    @DisplayName("월별 통계 조회 API 테스트")
    void getMonthlyStatistics() throws Exception {
        // given
        MonthlyStatisticsResponse response = MonthlyStatisticsResponse.builder()
            .yearMonth(YEAR_MONTH)
            .totalIncome(new BigDecimal("3000000"))
            .totalExpense(new BigDecimal("1000000"))
            .netIncome(new BigDecimal("2000000"))
            .build();

        when(statisticsService.getMonthlyStatistics(eq(USER_ID), eq(YEAR_MONTH)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/statistics/monthly")
                .param("userId", USER_ID)
                .param("yearMonth", YEAR_MONTH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.yearMonth").value(YEAR_MONTH))
            .andExpect(jsonPath("$.totalIncome").value(3000000))
            .andExpect(jsonPath("$.totalExpense").value(1000000))
            .andExpect(jsonPath("$.netIncome").value(2000000));
    }

    /**
     * 기간별 추이 조회 API 테스트
     * - HTTP 상태 코드 200 확인
     * - 응답 데이터 구조 및 값 확인
     */
    @Test
    @DisplayName("기간별 추이 조회 API 테스트")
    void getPeriodTrend() throws Exception {
        // given
        PeriodTrendResponse response = PeriodTrendResponse.builder()
            .startYearMonth(START_YEAR_MONTH)
            .endYearMonth(END_YEAR_MONTH)
            .build();

        when(statisticsService.getPeriodTrend(
            eq(USER_ID), eq(START_YEAR_MONTH), eq(END_YEAR_MONTH)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/statistics/trend")
                .param("userId", USER_ID)
                .param("startYearMonth", START_YEAR_MONTH)
                .param("endYearMonth", END_YEAR_MONTH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.startYearMonth").value(START_YEAR_MONTH))
            .andExpect(jsonPath("$.endYearMonth").value(END_YEAR_MONTH));
    }

    /**
     * 예산 대비 지출 통계 조회 API 테스트
     * - HTTP 상태 코드 200 확인
     * - 응답 데이터 구조 및 값 확인
     */
    @Test
    @DisplayName("예산 대비 지출 통계 조회 API 테스트")
    void getBudgetComparison() throws Exception {
        // given
        BudgetComparisonResponse response = BudgetComparisonResponse.builder()
            .yearMonth(YEAR_MONTH)
            .totalBudget(new BigDecimal("3000000"))
            .totalExpense(new BigDecimal("1000000"))
            .expenseRatio(33.33)
            .build();

        when(statisticsService.getBudgetComparison(eq(USER_ID), eq(YEAR_MONTH)))
            .thenReturn(response);

        // when & then
        mockMvc.perform(get("/api/statistics/budget-comparison")
                .param("userId", USER_ID)
                .param("yearMonth", YEAR_MONTH))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.yearMonth").value(YEAR_MONTH))
            .andExpect(jsonPath("$.totalBudget").value(3000000))
            .andExpect(jsonPath("$.totalExpense").value(1000000))
            .andExpect(jsonPath("$.expenseRatio").value(33.33));
    }
} 