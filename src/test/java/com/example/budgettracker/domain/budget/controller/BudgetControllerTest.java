package com.example.budgettracker.domain.budget.controller;

import com.example.budgettracker.domain.budget.dto.BudgetRequest;
import com.example.budgettracker.domain.budget.service.BudgetService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.YearMonth;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BudgetController.class)
class BudgetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BudgetService budgetService;

    @Test
    @WithMockUser
    @DisplayName("예산 생성 - 성공")
    void createBudget_Success() throws Exception {
        // given
        BudgetRequest request = new BudgetRequest();
        request.setAmount(BigDecimal.valueOf(1000000));
        request.setYearMonth(YearMonth.now());
        request.setCategoryId(1L);

        given(budgetService.createBudget(any(), any())).willReturn(1L);

        // when & then
        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("예산 생성 - 인증되지 않은 사용자")
    void createBudget_Unauthorized() throws Exception {
        // given
        BudgetRequest request = new BudgetRequest();
        request.setAmount(BigDecimal.valueOf(1000000));
        request.setYearMonth(YearMonth.now());
        request.setCategoryId(1L);

        // when & then
        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    @DisplayName("예산 생성 - 잘못된 입력값")
    void createBudget_InvalidInput() throws Exception {
        // given
        BudgetRequest request = new BudgetRequest();
        request.setAmount(BigDecimal.valueOf(-1000));
        request.setYearMonth(YearMonth.now());
        request.setCategoryId(1L);

        // when & then
        mockMvc.perform(post("/api/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
} 