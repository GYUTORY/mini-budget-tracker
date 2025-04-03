package com.example.budgettracker.domain.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@Schema(description = "월별 통계 응답")
public class MonthlyStatisticsResponse {
    
    @Schema(description = "년월", example = "2024-03")
    private String yearMonth;
    
    @Schema(description = "총 수입", example = "3000000")
    private BigDecimal totalIncome;
    
    @Schema(description = "총 지출", example = "2000000")
    private BigDecimal totalExpense;
    
    @Schema(description = "순수익 (수입 - 지출)", example = "1000000")
    private BigDecimal netIncome;
    
    @Schema(description = "카테고리별 지출 내역")
    private List<CategoryExpenseDto> categoryExpenses;
    
    @Getter
    @Builder
    public static class CategoryExpenseDto {
        @Schema(description = "카테고리 이름", example = "식비")
        private String categoryName;
        
        @Schema(description = "카테고리별 지출 금액", example = "500000")
        private BigDecimal amount;
        
        @Schema(description = "전체 지출 대비 비율", example = "25.0")
        private Double percentage;
    }
} 