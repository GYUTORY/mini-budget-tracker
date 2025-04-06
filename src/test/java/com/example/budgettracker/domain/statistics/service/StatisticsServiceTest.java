package com.example.budgettracker.domain.statistics.service;

import com.example.budgettracker.domain.statistics.dto.BudgetComparisonResponse;
import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
import com.example.budgettracker.domain.statistics.dto.PeriodTrendResponse;
import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.transaction.entity.TransactionType;
import com.example.budgettracker.domain.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

/**
 * StatisticsService의 단위 테스트 클래스
 * 
 * @ExtendWith: Mockito 확장 기능 사용
 * @InjectMocks: 테스트 대상 서비스 클래스
 * @Mock: 의존성 주입이 필요한 모의 객체
 */
@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private StatisticsService statisticsService;

    private static final String USER_ID = "test-user";
    private static final String YEAR_MONTH = "2024-03";
    private static final String START_YEAR_MONTH = "2024-01";
    private static final String END_YEAR_MONTH = "2024-03";

    /**
     * 테스트에 사용할 거래 내역 데이터를 생성합니다.
     */
    @BeforeEach
    void setUp() {
        // 월별 통계 테스트용 데이터
        List<Transaction> monthlyTransactions = Arrays.asList(
            createTransaction(TransactionType.INCOME, "급여", new BigDecimal("3000000")),
            createTransaction(TransactionType.EXPENSE, "식비", new BigDecimal("500000")),
            createTransaction(TransactionType.EXPENSE, "교통비", new BigDecimal("300000")),
            createTransaction(TransactionType.EXPENSE, "쇼핑", new BigDecimal("200000"))
        );

        // 기간별 추이 테스트용 데이터
        List<Transaction> trendTransactions = Arrays.asList(
            createTransaction(TransactionType.INCOME, "급여", new BigDecimal("3000000")),
            createTransaction(TransactionType.EXPENSE, "식비", new BigDecimal("400000")),
            createTransaction(TransactionType.EXPENSE, "교통비", new BigDecimal("200000")),
            createTransaction(TransactionType.EXPENSE, "쇼핑", new BigDecimal("100000"))
        );

        when(transactionRepository.findByUserIdAndDateBetween(
            eq(USER_ID), any(LocalDate.class), any(LocalDate.class)))
            .thenReturn(monthlyTransactions)
            .thenReturn(trendTransactions);
    }

    /**
     * 거래 내역 객체를 생성하는 헬퍼 메서드
     */
    private Transaction createTransaction(TransactionType type, String categoryName, BigDecimal amount) {
        return Transaction.builder()
            .userId(USER_ID)
            .type(type)
            .amount(amount)
            .categoryName(categoryName)
            .date(LocalDate.now())
            .build();
    }

    /**
     * 월별 통계 조회 테스트
     * - 수입, 지출, 순수익 계산이 정확한지 확인
     * - 카테고리별 지출 비율이 정확한지 확인
     */
    @Test
    @DisplayName("월별 통계 조회 테스트")
    void getMonthlyStatistics() {
        // when
        MonthlyStatisticsResponse response = statisticsService.getMonthlyStatistics(USER_ID, YEAR_MONTH);

        // then
        assertNotNull(response);
        assertEquals(YEAR_MONTH, response.getYearMonth());
        assertEquals(new BigDecimal("3000000"), response.getTotalIncome());
        assertEquals(new BigDecimal("1000000"), response.getTotalExpense());
        assertEquals(new BigDecimal("2000000"), response.getNetIncome());
        
        // 카테고리별 지출 비율 확인
        assertEquals(3, response.getCategoryExpenses().size());
        response.getCategoryExpenses().forEach(category -> {
            switch (category.getCategoryName()) {
                case "식비":
                    assertEquals(new BigDecimal("500000"), category.getAmount());
                    assertEquals(50.0, category.getPercentage());
                    break;
                case "교통비":
                    assertEquals(new BigDecimal("300000"), category.getAmount());
                    assertEquals(30.0, category.getPercentage());
                    break;
                case "쇼핑":
                    assertEquals(new BigDecimal("200000"), category.getAmount());
                    assertEquals(20.0, category.getPercentage());
                    break;
            }
        });
    }

    /**
     * 기간별 추이 조회 테스트
     * - 각 월별 수입, 지출, 순수익이 정확한지 확인
     */
    @Test
    @DisplayName("기간별 추이 조회 테스트")
    void getPeriodTrend() {
        // when
        PeriodTrendResponse response = statisticsService.getPeriodTrend(
            USER_ID, START_YEAR_MONTH, END_YEAR_MONTH);

        // then
        assertNotNull(response);
        assertEquals(START_YEAR_MONTH, response.getStartYearMonth());
        assertEquals(END_YEAR_MONTH, response.getEndYearMonth());
        
        // 월별 추이 확인
        assertEquals(3, response.getMonthlyTrends().size());
        response.getMonthlyTrends().forEach(monthly -> {
            assertEquals(new BigDecimal("3000000"), monthly.getIncome());
            assertEquals(new BigDecimal("700000"), monthly.getExpense());
            assertEquals(new BigDecimal("2300000"), monthly.getNetIncome());
        });
    }

    /**
     * 예산 대비 지출 통계 조회 테스트
     * - 총 예산 대비 지출 비율이 정확한지 확인
     * - 카테고리별 예산 대비 지출 비율이 정확한지 확인
     */
    @Test
    @DisplayName("예산 대비 지출 통계 조회 테스트")
    void getBudgetComparison() {
        // when
        BudgetComparisonResponse response = statisticsService.getBudgetComparison(USER_ID, YEAR_MONTH);

        // then
        assertNotNull(response);
        assertEquals(YEAR_MONTH, response.getYearMonth());
        assertEquals(new BigDecimal("3000000"), response.getTotalBudget());
        assertEquals(new BigDecimal("1000000"), response.getTotalExpense());
        assertEquals(33.33, response.getExpenseRatio(), 0.01);
        
        // 카테고리별 예산 대비 지출 비율 확인
        assertEquals(3, response.getCategoryBudgets().size());
        response.getCategoryBudgets().forEach(category -> {
            switch (category.getCategoryName()) {
                case "식비":
                    assertEquals(new BigDecimal("500000"), category.getBudget());
                    assertEquals(new BigDecimal("500000"), category.getExpense());
                    assertEquals(100.0, category.getRatio());
                    break;
                case "교통비":
                    assertEquals(new BigDecimal("300000"), category.getBudget());
                    assertEquals(new BigDecimal("300000"), category.getExpense());
                    assertEquals(100.0, category.getRatio());
                    break;
                case "쇼핑":
                    assertEquals(new BigDecimal("200000"), category.getBudget());
                    assertEquals(new BigDecimal("200000"), category.getExpense());
                    assertEquals(100.0, category.getRatio());
                    break;
            }
        });
    }
} 