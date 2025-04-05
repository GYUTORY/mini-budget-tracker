package com.example.budgettracker.domain.statistics.controller

import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse
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

    private def createAuthentication(String userId) {
        def authentication = Mock(org.springframework.security.core.Authentication)
        authentication.getName() >> userId
        return authentication
    }
} 