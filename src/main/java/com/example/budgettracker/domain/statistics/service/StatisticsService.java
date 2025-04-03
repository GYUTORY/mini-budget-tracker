package com.example.budgettracker.domain.statistics.service;

import com.example.budgettracker.domain.statistics.dto.MonthlyStatisticsResponse;
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
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatisticsService {

    private final TransactionRepository transactionRepository;

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
} 