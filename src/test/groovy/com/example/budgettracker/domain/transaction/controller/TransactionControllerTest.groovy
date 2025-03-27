package com.example.budgettracker.domain.transaction.controller

import com.example.budgettracker.domain.transaction.dto.TransactionRequest
import com.example.budgettracker.domain.transaction.dto.TransactionResponse
import com.example.budgettracker.domain.transaction.entity.Category
import com.example.budgettracker.domain.transaction.entity.TransactionType
import com.example.budgettracker.domain.transaction.service.TransactionService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.security.test.context.support.WithMockUser
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import java.math.BigDecimal
import java.time.LocalDateTime

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(TransactionController)
class TransactionControllerTest extends Specification {

    @Autowired
    private MockMvc mockMvc

    @Autowired
    private ObjectMapper objectMapper

    @MockBean
    private TransactionService transactionService

    @WithMockUser
    def "거래 등록 성공"() {
        given:
        def request = new TransactionRequest(
            type: TransactionType.INCOME,
            category: Category.SALARY,
            amount: new BigDecimal("5000000"),
            transactionDate: LocalDateTime.now(),
            memo: "3월 급여"
        )
        def response = new TransactionResponse(
            id: "transaction-id",
            type: request.getType(),
            category: request.getCategory(),
            amount: request.getAmount(),
            transactionDate: request.getTransactionDate(),
            memo: request.getMemo()
        )
        
        and:
        transactionService.createTransaction(_, request) >> response

        when:
        def result = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath("$.success").value(true))
        result.andExpect(jsonPath("$.data.type").value(request.getType().toString()))
        result.andExpect(jsonPath("$.data.category").value(request.getCategory().toString()))
        result.andExpect(jsonPath("$.data.amount").value(request.getAmount().toString()))
        result.andExpect(jsonPath("$.data.memo").value(request.getMemo()))
    }

    @WithMockUser
    def "거래 등록 실패 - 잘못된 요청"() {
        given:
        def request = new TransactionRequest(
            type: null,  // 필수 필드 누락
            category: null,  // 필수 필드 누락
            amount: new BigDecimal("-1000"),  // 음수 금액
            transactionDate: null,  // 필수 필드 누락
            memo: ""  // 빈 메모
        )

        when:
        def result = mockMvc.perform(post("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))

        then:
        result.andExpect(status().isBadRequest())
    }

    @WithMockUser
    def "거래 목록 조회 성공"() {
        given:
        def userId = "test-user-id"
        def response = [
            new TransactionResponse(
                id: "transaction-1",
                type: TransactionType.INCOME,
                category: Category.SALARY,
                amount: new BigDecimal("5000000"),
                transactionDate: LocalDateTime.now(),
                memo: "3월 급여"
            ),
            new TransactionResponse(
                id: "transaction-2",
                type: TransactionType.EXPENSE,
                category: Category.FOOD,
                amount: new BigDecimal("50000"),
                transactionDate: LocalDateTime.now(),
                memo: "점심 식사"
            )
        ]
        
        and:
        transactionService.getUserTransactions(userId) >> response

        when:
        def result = mockMvc.perform(get("/api/transactions"))

        then:
        result.andExpect(status().isOk())
        result.andExpect(jsonPath("$.success").value(true))
        result.andExpect(jsonPath("$.data.length()").value(2))
        result.andExpect(jsonPath("$.data[0].type").value(TransactionType.INCOME.toString()))
        result.andExpect(jsonPath("$.data[1].type").value(TransactionType.EXPENSE.toString()))
    }

    def "거래 목록 조회 실패 - 인증되지 않은 사용자"() {
        when:
        def result = mockMvc.perform(get("/api/transactions"))

        then:
        result.andExpect(status().isUnauthorized())
    }
} 