package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.entity.Category;
import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private String id;
    private TransactionType type;
    private Category category;
    private BigDecimal amount;
    private LocalDateTime transactionDate;
    private String memo;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .amount(transaction.getAmount())
                .transactionDate(transaction.getTransactionDate())
                .memo(transaction.getMemo())
                .createdAt(transaction.getCreatedAt())
                .updatedAt(transaction.getUpdatedAt())
                .build();
    }
} 