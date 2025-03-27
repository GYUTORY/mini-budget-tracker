package com.example.budgettracker.domain.transaction.service

import com.example.budgettracker.domain.transaction.dto.TransactionRequest
import com.example.budgettracker.domain.transaction.dto.TransactionResponse
import com.example.budgettracker.domain.transaction.entity.Category
import com.example.budgettracker.domain.transaction.entity.Transaction
import com.example.budgettracker.domain.transaction.entity.TransactionType
import com.example.budgettracker.domain.transaction.repository.TransactionRepository
import com.example.budgettracker.domain.user.entity.User
import com.example.budgettracker.domain.user.repository.UserRepository
import com.example.budgettracker.global.exception.BusinessException
import spock.lang.Specification
import spock.lang.Subject

import java.math.BigDecimal
import java.time.LocalDateTime

class TransactionServiceTest extends Specification {

    TransactionRepository transactionRepository = Mock()
    UserRepository userRepository = Mock()

    @Subject
    TransactionService transactionService = new TransactionService(transactionRepository, userRepository)

    def "거래 등록 성공"() {
        given:
        def userId = "test-user-id"
        def request = new TransactionRequest(
            type: TransactionType.INCOME,
            category: Category.SALARY,
            amount: new BigDecimal("5000000"),
            transactionDate: LocalDateTime.now(),
            memo: "3월 급여"
        )
        
        and:
        def user = new User(id: userId)
        userRepository.findById(userId) >> Optional.of(user)
        transactionRepository.save(_ as Transaction) >> new Transaction(
            id: "transaction-id",
            user: user,
            type: request.getType(),
            category: request.getCategory(),
            amount: request.getAmount(),
            transactionDate: request.getTransactionDate(),
            memo: request.getMemo()
        )

        when:
        def result = transactionService.createTransaction(userId, request)

        then:
        result != null
        result.getType() == request.getType()
        result.getCategory() == request.getCategory()
        result.getAmount() == request.getAmount()
        result.getMemo() == request.getMemo()
        1 * userRepository.findById(userId)
        1 * transactionRepository.save(_ as Transaction)
    }

    def "거래 등록 실패 - 사용자 없음"() {
        given:
        def userId = "test-user-id"
        def request = new TransactionRequest(
            type: TransactionType.INCOME,
            category: Category.SALARY,
            amount: new BigDecimal("5000000"),
            transactionDate: LocalDateTime.now(),
            memo: "3월 급여"
        )
        
        and:
        userRepository.findById(userId) >> Optional.empty()

        when:
        transactionService.createTransaction(userId, request)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "사용자를 찾을 수 없습니다."
    }

    def "거래 목록 조회 성공"() {
        given:
        def userId = "test-user-id"
        def user = new User(id: userId)
        
        and:
        userRepository.findById(userId) >> Optional.of(user)
        def transactions = [
            new Transaction(
                id: "transaction-1",
                user: user,
                type: TransactionType.INCOME,
                category: Category.SALARY,
                amount: new BigDecimal("5000000"),
                transactionDate: LocalDateTime.now(),
                memo: "3월 급여"
            ),
            new Transaction(
                id: "transaction-2",
                user: user,
                type: TransactionType.EXPENSE,
                category: Category.FOOD,
                amount: new BigDecimal("50000"),
                transactionDate: LocalDateTime.now(),
                memo: "점심 식사"
            )
        ]
        transactionRepository.findByUserOrderByTransactionDateDesc(user) >> transactions

        when:
        def result = transactionService.getUserTransactions(userId)

        then:
        result != null
        result.size() == 2
        result[0].getType() == TransactionType.INCOME
        result[1].getType() == TransactionType.EXPENSE
        1 * userRepository.findById(userId)
        1 * transactionRepository.findByUserOrderByTransactionDateDesc(user)
    }

    def "거래 목록 조회 실패 - 사용자 없음"() {
        given:
        def userId = "test-user-id"
        
        and:
        userRepository.findById(userId) >> Optional.empty()

        when:
        transactionService.getUserTransactions(userId)

        then:
        thrown(BusinessException)
        thrown(BusinessException).message == "사용자를 찾을 수 없습니다."
    }
} 