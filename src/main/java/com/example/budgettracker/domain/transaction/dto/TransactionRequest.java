package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.entity.Category;
import com.example.budgettracker.domain.transaction.entity.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "거래 내역 생성/수정 요청")
public class TransactionRequest {

    @Schema(description = "거래 금액", example = "50000")
    @NotNull(message = "금액은 필수입니다.")
    @Positive(message = "금액은 양수여야 합니다.")
    private BigDecimal amount;

    @Schema(description = "거래 유형 (INCOME: 수입, EXPENSE: 지출)", example = "EXPENSE")
    @NotNull(message = "거래 유형은 필수입니다.")
    private TransactionType type;

    @Schema(description = "거래 카테고리", example = "FOOD")
    @NotNull(message = "카테고리는 필수입니다.")
    private Category category;

    @Schema(description = "거래 설명", example = "점심 식사")
    @NotNull(message = "설명은 필수입니다.")
    private String description;

    @Schema(description = "거래 일시", example = "2024-03-28T12:00:00")
    @NotNull(message = "날짜는 필수입니다.")
    private LocalDateTime date;
} 