package com.example.budgettracker.domain.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 예산 대비 지출 통계를 담는 응답 DTO
 * 
 * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
 * @Builder: Lombok을 사용하여 빌더 패턴 자동 생성
 * @Schema: Swagger 문서 생성을 위한 스키마 정보
 */
@Getter
@Builder
@Schema(description = "예산 대비 지출 통계 응답")
public class BudgetComparisonResponse {
    
    /**
     * 조회 대상 년월 (yyyy-MM 형식)
     */
    @Schema(description = "년월", example = "2024-03")
    private String yearMonth;
    
    /**
     * 전체 예산 금액
     */
    @Schema(description = "전체 예산", example = "3000000")
    private BigDecimal totalBudget;
    
    /**
     * 전체 지출 금액
     */
    @Schema(description = "전체 지출", example = "2000000")
    private BigDecimal totalExpense;
    
    /**
     * 예산 대비 지출 비율 (전체 지출 / 전체 예산 * 100)
     */
    @Schema(description = "예산 대비 지출 비율", example = "66.67")
    private Double expenseRatio;
    
    /**
     * 카테고리별 예산 대비 지출 데이터 목록
     */
    @Schema(description = "카테고리별 예산 대비 지출")
    private List<CategoryBudgetDto> categoryBudgets;
    
    /**
     * 카테고리별 예산 대비 지출 데이터를 담는 내부 DTO
     * 
     * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
     * @Builder: Lombok을 사용하여 빌더 패턴 자동 생성
     */
    @Getter
    @Builder
    public static class CategoryBudgetDto {
        /**
         * 카테고리 이름
         */
        @Schema(description = "카테고리 이름", example = "식비")
        private String categoryName;
        
        /**
         * 해당 카테고리의 예산 금액
         */
        @Schema(description = "카테고리 예산", example = "500000")
        private BigDecimal budget;
        
        /**
         * 해당 카테고리의 실제 지출 금액
         */
        @Schema(description = "카테고리 지출", example = "400000")
        private BigDecimal expense;
        
        /**
         * 카테고리 예산 대비 지출 비율 (지출 / 예산 * 100)
         */
        @Schema(description = "예산 대비 지출 비율", example = "80.0")
        private Double ratio;
    }
} 