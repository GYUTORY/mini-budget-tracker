package com.example.budgettracker.domain.budget.repository;

import com.example.budgettracker.domain.budget.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.YearMonth;
import java.util.List;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    @Query("SELECT b FROM Budget b WHERE b.user.id = :userId AND b.yearMonth = :yearMonth")
    List<Budget> findByUserIdAndYearMonth(@Param("userId") Long userId, @Param("yearMonth") YearMonth yearMonth);
} 