package com.example.budgettracker.domain.statistics.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

/**
 * 기간별 수입/지출 추이 통계를 담는 응답 DTO
 * 
 * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
 * @Builder: Lombok을 사용하여 빌더 패턴 자동 생성
 * @Schema: Swagger 문서 생성을 위한 스키마 정보
 */
@Getter
@Builder
@Schema(description = "기간별 추이 통계 응답")
public class PeriodTrendResponse {
    
    /**
     * 조회 기간의 시작 년월 (yyyy-MM 형식)
     */
    @Schema(description = "시작 년월", example = "2024-01")
    private String startYearMonth;
    
    /**
     * 조회 기간의 종료 년월 (yyyy-MM 형식)
     */
    @Schema(description = "종료 년월", example = "2024-03")
    private String endYearMonth;
    
    /**
     * 월별 수입/지출 추이 데이터 목록
     */
    @Schema(description = "월별 수입/지출 추이")
    private List<MonthlyTrendDto> monthlyTrends;
    
    /**
     * 월별 수입/지출 추이 데이터를 담는 내부 DTO
     * 
     * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
     * @Builder: Lombok을 사용하여 빌더 패턴 자동 생성
     */
    @Getter
    @Builder
    public static class MonthlyTrendDto {
        /**
         * 해당 월의 년월 (yyyy-MM 형식)
         */
        @Schema(description = "년월", example = "2024-01")
        private String yearMonth;
        
        /**
         * 해당 월의 총 수입 금액
         */
        @Schema(description = "월 수입", example = "3000000")
        private BigDecimal income;
        
        /**
         * 해당 월의 총 지출 금액
         */
        @Schema(description = "월 지출", example = "2000000")
        private BigDecimal expense;
        
        /**
         * 해당 월의 순수익 (수입 - 지출)
         */
        @Schema(description = "월 순수익", example = "1000000")
        private BigDecimal netIncome;
    }
} 