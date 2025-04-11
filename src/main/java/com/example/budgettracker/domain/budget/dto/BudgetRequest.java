package com.example.budgettracker.domain.budget.dto;

import lombok.Getter;
import lombok.Setter;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.YearMonth;

@Getter
@Setter
public class BudgetRequest {
    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    private BigDecimal amount;

    @NotNull(message = "년월은 필수입니다.")
    private YearMonth yearMonth;

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;
} 