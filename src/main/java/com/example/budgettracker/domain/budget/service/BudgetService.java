package com.example.budgettracker.domain.budget.service;

import com.example.budgettracker.domain.budget.dto.BudgetRequest;
import com.example.budgettracker.domain.budget.dto.BudgetResponse;
import com.example.budgettracker.domain.budget.entity.Budget;
import com.example.budgettracker.domain.budget.repository.BudgetRepository;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.BusinessException;
import com.example.budgettracker.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;

    @Transactional
    public Long createBudget(Long userId, BudgetRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (!budgetRepository.findByUserIdAndYearMonth(userId, request.getYearMonth()).isEmpty()) {
            throw new BusinessException(ErrorCode.BUDGET_PERIOD_OVERLAP);
        }

        Budget budget = Budget.builder()
                .user(user)
                .amount(request.getAmount())
                .yearMonth(request.getYearMonth())
                .categoryId(request.getCategoryId())
                .build();

        return budgetRepository.save(budget).getId();
    }

    public BudgetResponse getBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        return BudgetResponse.from(budget);
    }

    public List<BudgetResponse> getBudgets(Long userId) {
        return budgetRepository.findAll().stream()
                .filter(budget -> budget.getUser().getId().equals(userId))
                .map(BudgetResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Long updateBudget(Long userId, Long budgetId, BudgetRequest request) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        List<Budget> existingBudgets = budgetRepository.findByUserIdAndYearMonth(userId, request.getYearMonth());
        if (!existingBudgets.isEmpty() && !existingBudgets.get(0).getId().equals(budgetId)) {
            throw new BusinessException(ErrorCode.BUDGET_PERIOD_OVERLAP);
        }

        Budget updatedBudget = Budget.builder()
                .id(budgetId)
                .user(budget.getUser())
                .amount(request.getAmount())
                .yearMonth(request.getYearMonth())
                .categoryId(request.getCategoryId())
                .build();

        return budgetRepository.save(updatedBudget).getId();
    }

    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new BusinessException(ErrorCode.BUDGET_NOT_FOUND));

        if (!budget.getUser().getId().equals(userId)) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }

        budgetRepository.delete(budget);
    }
} 