package com.example.budgettracker.domain.statistics.service

import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse
import com.example.budgettracker.domain.transaction.entity.Transaction
import com.example.budgettracker.domain.transaction.entity.TransactionType
import com.example.budgettracker.domain.transaction.repository.TransactionRepository
import spock.lang.Specification
import spock.lang.Subject

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

class StatisticsServiceSpec extends Specification {

    def transactionRepository = Mock(TransactionRepository)
    @Subject
    def statisticsService = new StatisticsService(transactionRepository)

    def "월별 통계를 정상적으로 계산한다"() {
        given:
        def userId = "test-user"
        def yearMonth = "2024-03"
        def startDate = LocalDate.of(2024, 3, 1)
        def endDate = LocalDate.of(2024, 3, 31)

        def transactions = [
            createTransaction(TransactionType.INCOME, "급여", new BigDecimal("3000000")),
            createTransaction(TransactionType.EXPENSE, "식비", new BigDecimal("500000")),
            createTransaction(TransactionType.EXPENSE, "교통비", new BigDecimal("300000")),
            createTransaction(TransactionType.EXPENSE, "쇼핑", new BigDecimal("200000"))
        ]

        transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate) >> transactions

        when:
        def result = statisticsService.getMonthlyStatistics(userId, yearMonth)

        then:
        result.yearMonth == yearMonth
        result.totalIncome == new BigDecimal("3000000")
        result.totalExpense == new BigDecimal("1000000")
        result.netIncome == new BigDecimal("2000000")
        result.categoryExpenses.size() == 3
        result.categoryExpenses.find { it.categoryName == "식비" }.amount == new BigDecimal("500000")
        result.categoryExpenses.find { it.categoryName == "식비" }.percentage == 50.0
        result.categoryExpenses.find { it.categoryName == "교통비" }.amount == new BigDecimal("300000")
        result.categoryExpenses.find { it.categoryName == "교통비" }.percentage == 30.0
        result.categoryExpenses.find { it.categoryName == "쇼핑" }.amount == new BigDecimal("200000")
        result.categoryExpenses.find { it.categoryName == "쇼핑" }.percentage == 20.0
    }

    def "거래 내역이 없는 경우 0으로 계산한다"() {
        given:
        def userId = "test-user"
        def yearMonth = "2024-03"
        def startDate = LocalDate.of(2024, 3, 1)
        def endDate = LocalDate.of(2024, 3, 31)

        transactionRepository.findByUserIdAndDateBetween(userId, startDate, endDate) >> []

        when:
        def result = statisticsService.getMonthlyStatistics(userId, yearMonth)

        then:
        result.yearMonth == yearMonth
        result.totalIncome == BigDecimal.ZERO
        result.totalExpense == BigDecimal.ZERO
        result.netIncome == BigDecimal.ZERO
        result.categoryExpenses.isEmpty()
    }

    private Transaction createTransaction(TransactionType type, String categoryName, BigDecimal amount) {
        return Transaction.builder()
            .type(type)
            .amount(amount)
            .category(createCategory(categoryName))
            .date(LocalDateTime.now())
            .description("테스트 거래")
            .build()
    }

    private def createCategory(String name) {
        def category = new com.example.budgettracker.domain.transaction.entity.Category()
        category.name = name
        return category
    }
} 