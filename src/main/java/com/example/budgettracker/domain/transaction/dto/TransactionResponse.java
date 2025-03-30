package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.entity.Category;
import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.transaction.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "거래 내역 응답")
public class TransactionResponse {

    @Schema(description = "거래 내역 ID", example = "1")
    private Long id;

    @Schema(description = "거래 금액", example = "50000")
    private BigDecimal amount;

    @Schema(description = "거래 유형 (INCOME: 수입, EXPENSE: 지출)", example = "EXPENSE")
    private TransactionType type;

    @Schema(description = "거래 카테고리", example = "FOOD")
    private Category category;

    @Schema(description = "거래 설명", example = "점심 식사")
    private String description;

    @Schema(description = "거래 일시", example = "2024-03-28T12:00:00")
    private LocalDateTime date;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .amount(transaction.getAmount())
                .type(transaction.getType())
                .category(transaction.getCategory())
                .description(transaction.getDescription())
                .date(transaction.getDate())
                .build();
    }
} 