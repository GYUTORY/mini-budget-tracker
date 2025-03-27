package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.Category;
import com.example.budgettracker.domain.transaction.TransactionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull
    private TransactionType type;

    @NotNull
    private Category category;

    @NotNull
    @Positive
    private BigDecimal amount;

    @NotNull
    private LocalDateTime transactionDate;

    private String memo;
} 