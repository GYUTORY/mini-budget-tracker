package com.example.budgettracker.domain.budget.dto;

import com.example.budgettracker.domain.budget.entity.Budget;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BudgetResponse {
    private Long id;
    private BigDecimal amount;
    private YearMonth yearMonth;
    private Long categoryId;

    public static BudgetResponse from(Budget budget) {
        return BudgetResponse.builder()
                .id(budget.getId())
                .amount(budget.getAmount())
                .yearMonth(budget.getYearMonth())
                .categoryId(budget.getCategoryId())
                .build();
    }
} 