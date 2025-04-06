package com.example.budgettracker.domain.statistics.controller

import com.example.budgettracker.domain.statistics.dto.BudgetComparisonResponse
import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse
import com.example.budgettracker.domain.statistics.dto.PeriodTrendResponse
import com.example.budgettracker.domain.statistics.service.StatisticsService
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Subject

import java.math.BigDecimal
import java.time.YearMonth

class StatisticsControllerSpec extends Specification {

    def statisticsService = Mock(StatisticsService)
    @Subject
    def statisticsController = new StatisticsController(statisticsService)

    def "월별 통계를 정상적으로 조회한다"() {
        given:
        def userId = "test-user"
        def yearMonth = YearMonth.of(2024, 3)
        def expectedResponse = createMonthlyStatisticsResponse()

        statisticsService.getMonthlyStatistics(userId, "2024-03") >> expectedResponse

        when:
        def response = statisticsController.getMonthlyStatistics(
            createAuthentication(userId),
            yearMonth
        )

        then:
        response.statusCode == HttpStatus.OK
        response.body == expectedResponse
    }

    def "기간별 추이를 정상적으로 조회한다"() {
        given:
        def userId = "test-user"
        def startYearMonth = YearMonth.of(2024, 1)
        def endYearMonth = YearMonth.of(2024, 3)
        def expectedResponse = createPeriodTrendResponse()

        statisticsService.getPeriodTrend(userId, "2024-01", "2024-03") >> expectedResponse

        when:
        def response = statisticsController.getPeriodTrend(
            createAuthentication(userId),
            startYearMonth,
            endYearMonth
        )

        then:
        response.statusCode == HttpStatus.OK
        response.body == expectedResponse
    }
    
    def "예산 대비 지출을 정상적으로 조회한다"() {
        given:
        def userId = "test-user"
        def yearMonth = YearMonth.of(2024, 3)
        def expectedResponse = createBudgetComparisonResponse()

        statisticsService.getBudgetComparison(userId, "2024-03") >> expectedResponse

        when:
        def response = statisticsController.getBudgetComparison(
            createAuthentication(userId),
            yearMonth
        )

        then:
        response.statusCode == HttpStatus.OK
        response.body == expectedResponse
    }

    private MonthlyStatisticsResponse createMonthlyStatisticsResponse() {
        return MonthlyStatisticsResponse.builder()
            .yearMonth("2024-03")
            .totalIncome(new BigDecimal("3000000"))
            .totalExpense(new BigDecimal("1000000"))
            .netIncome(new BigDecimal("2000000"))
            .categoryExpenses([
                MonthlyStatisticsResponse.CategoryExpenseDto.builder()
                    .categoryName("식비")
                    .amount(new BigDecimal("500000"))
                    .percentage(50.0)
                    .build(),
                MonthlyStatisticsResponse.CategoryExpenseDto.builder()
                    .categoryName("교통비")
                    .amount(new BigDecimal("300000"))
                    .percentage(30.0)
                    .build(),
                MonthlyStatisticsResponse.CategoryExpenseDto.builder()
                    .categoryName("쇼핑")
                    .amount(new BigDecimal("200000"))
                    .percentage(20.0)
                    .build()
            ])
            .build()
    }

    private PeriodTrendResponse createPeriodTrendResponse() {
        return PeriodTrendResponse.builder()
            .startYearMonth("2024-01")
            .endYearMonth("2024-03")
            .monthlyTrends([
                PeriodTrendResponse.MonthlyTrendDto.builder()
                    .yearMonth("2024-01")
                    .income(new BigDecimal("3000000"))
                    .expense(new BigDecimal("600000"))
                    .netIncome(new BigDecimal("2400000"))
                    .build(),
                PeriodTrendResponse.MonthlyTrendDto.builder()
                    .yearMonth("2024-02")
                    .income(new BigDecimal("3000000"))
                    .expense(new BigDecimal("700000"))
                    .netIncome(new BigDecimal("2300000"))
                    .build(),
                PeriodTrendResponse.MonthlyTrendDto.builder()
                    .yearMonth("2024-03")
                    .income(new BigDecimal("3000000"))
                    .expense(new BigDecimal("800000"))
                    .netIncome(new BigDecimal("2200000"))
                    .build()
            ])
            .build()
    }
    
    private BudgetComparisonResponse createBudgetComparisonResponse() {
        return BudgetComparisonResponse.builder()
            .yearMonth("2024-03")
            .totalBudget(new BigDecimal("3000000"))
            .totalExpense(new BigDecimal("800000"))
            .expenseRatio(26.67)
            .categoryBudgets([
                BudgetComparisonResponse.CategoryBudgetDto.builder()
                    .categoryName("식비")
                    .budget(new BigDecimal("500000"))
                    .expense(new BigDecimal("400000"))
                    .ratio(80.0)
                    .build(),
                BudgetComparisonResponse.CategoryBudgetDto.builder()
                    .categoryName("교통비")
                    .budget(new BigDecimal("300000"))
                    .expense(new BigDecimal("250000"))
                    .ratio(83.33)
                    .build(),
                BudgetComparisonResponse.CategoryBudgetDto.builder()
                    .categoryName("쇼핑")
                    .budget(new BigDecimal("200000"))
                    .expense(new BigDecimal("150000"))
                    .ratio(75.0)
                    .build()
            ])
            .build()
    }

    private def createAuthentication(String userId) {
        def authentication = Mock(org.springframework.security.core.Authentication)
        authentication.getName() >> userId
        return authentication
    }
} 