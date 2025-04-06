package com.example.budgettracker.domain.statistics.service;

import com.example.budgettracker.domain.statistics.dto.BudgetComparisonResponse;
import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
import com.example.budgettracker.domain.statistics.dto.PeriodTrendResponse;
import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.transaction.entity.TransactionType;
import com.example.budgettracker.domain.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 통계 및 분석 관련 비즈니스 로직을 처리하는 서비스
 * 
 * @Service: Spring 서비스 컴포넌트임을 명시
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 * @Transactional: 트랜잭션 설정 (기본적으로 읽기 전용)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    /**
     * 월별 통계 정보를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param yearMonth 조회할 년월 (yyyy-MM 형식)
     * @return 월별 통계 응답 DTO
     */
    public MonthlyStatisticsResponse getMonthlyStatistics(String userId, String yearMonth) {
        // 입력된 년월을 YearMonth 객체로 변환
        YearMonth targetMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        
        // 해당 월의 시작일과 종료일
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();
        
        // 해당 월의 모든 거래 내역 조회
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
            userId, startDate, endDate);
        
        // 수입과 지출 분리
        BigDecimal totalIncome = transactions.stream()
            .filter(t -> t.getType() == TransactionType.INCOME)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        BigDecimal totalExpense = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        // 카테고리별 지출 계산
        Map<String, BigDecimal> categoryExpenses = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .collect(Collectors.groupingBy(
                t -> t.getCategory().getName(),
                Collectors.mapping(
                    Transaction::getAmount,
                    Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                )
            ));
            
        // 카테고리별 지출 비율 계산
        List<MonthlyStatisticsResponse.CategoryExpenseDto> categoryExpenseDtos = 
            categoryExpenses.entrySet().stream()
                .map(entry -> {
                    double percentage = totalExpense.compareTo(BigDecimal.ZERO) > 0
                        ? entry.getValue().multiply(new BigDecimal("100"))
                            .divide(totalExpense, 2, RoundingMode.HALF_UP)
                            .doubleValue()
                        : 0.0;
                        
                    return MonthlyStatisticsResponse.CategoryExpenseDto.builder()
                        .categoryName(entry.getKey())
                        .amount(entry.getValue())
                        .percentage(percentage)
                        .build();
                })
                .collect(Collectors.toList());
        
        // 순수익 계산
        BigDecimal netIncome = totalIncome.subtract(totalExpense);
        
        return MonthlyStatisticsResponse.builder()
            .yearMonth(yearMonth)
            .totalIncome(totalIncome)
            .totalExpense(totalExpense)
            .netIncome(netIncome)
            .categoryExpenses(categoryExpenseDtos)
            .build();
    }

    /**
     * 기간별 수입/지출 추이를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param startYearMonth 시작 년월 (yyyy-MM 형식)
     * @param endYearMonth 종료 년월 (yyyy-MM 형식)
     * @return 기간별 추이 응답 DTO
     */
    public PeriodTrendResponse getPeriodTrend(String userId, String startYearMonth, String endYearMonth) {
        YearMonth start = YearMonth.parse(startYearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        YearMonth end = YearMonth.parse(endYearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        
        List<PeriodTrendResponse.MonthlyTrendDto> monthlyTrends = new ArrayList<>();
        
        for (YearMonth current = start; !current.isAfter(end); current = current.plusMonths(1)) {
            LocalDate startDate = current.atDay(1);
            LocalDate endDate = current.atEndOfMonth();
            
            List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
                userId, startDate, endDate);
            
            BigDecimal income = transactions.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal expense = transactions.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
                
            BigDecimal netIncome = income.subtract(expense);
            
            monthlyTrends.add(PeriodTrendResponse.MonthlyTrendDto.builder()
                .yearMonth(current.format(DateTimeFormatter.ofPattern("yyyy-MM")))
                .income(income)
                .expense(expense)
                .netIncome(netIncome)
                .build());
        }
        
        return PeriodTrendResponse.builder()
            .startYearMonth(startYearMonth)
            .endYearMonth(endYearMonth)
            .monthlyTrends(monthlyTrends)
            .build();
    }
    
    /**
     * 예산 대비 지출 통계를 조회합니다.
     * 
     * @param userId 사용자 ID
     * @param yearMonth 조회할 년월 (yyyy-MM 형식)
     * @return 예산 대비 지출 응답 DTO
     */
    public BudgetComparisonResponse getBudgetComparison(String userId, String yearMonth) {
        YearMonth targetMonth = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = targetMonth.atDay(1);
        LocalDate endDate = targetMonth.atEndOfMonth();
        
        // TODO: 예산 정보를 가져오는 로직 추가 (현재는 하드코딩된 값 사용)
        BigDecimal totalBudget = new BigDecimal("3000000");
        Map<String, BigDecimal> categoryBudgets = Map.of(
            "식비", new BigDecimal("500000"),
            "교통비", new BigDecimal("300000"),
            "쇼핑", new BigDecimal("200000")
        );
        
        List<Transaction> transactions = transactionRepository.findByUserIdAndDateBetween(
            userId, startDate, endDate);
            
        BigDecimal totalExpense = transactions.stream()
            .filter(t -> t.getType() == TransactionType.EXPENSE)
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
            
        double expenseRatio = totalBudget.compareTo(BigDecimal.ZERO) > 0
            ? totalExpense.multiply(new BigDecimal("100"))
                .divide(totalBudget, 2, RoundingMode.HALF_UP)
                .doubleValue()
            : 0.0;
            
        List<BudgetComparisonResponse.CategoryBudgetDto> categoryBudgetDtos = 
            categoryBudgets.entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    BigDecimal budget = entry.getValue();
                    
                    BigDecimal expense = transactions.stream()
                        .filter(t -> t.getType() == TransactionType.EXPENSE)
                        .filter(t -> t.getCategory().getName().equals(categoryName))
                        .map(Transaction::getAmount)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                        
                    double ratio = budget.compareTo(BigDecimal.ZERO) > 0
                        ? expense.multiply(new BigDecimal("100"))
                            .divide(budget, 2, RoundingMode.HALF_UP)
                            .doubleValue()
                        : 0.0;
                        
                    return BudgetComparisonResponse.CategoryBudgetDto.builder()
                        .categoryName(categoryName)
                        .budget(budget)
                        .expense(expense)
                        .ratio(ratio)
                        .build();
                })
                .collect(Collectors.toList());
                
        return BudgetComparisonResponse.builder()
            .yearMonth(yearMonth)
            .totalBudget(totalBudget)
            .totalExpense(totalExpense)
            .expenseRatio(expenseRatio)
            .categoryBudgets(categoryBudgetDtos)
            .build();
    }
} 