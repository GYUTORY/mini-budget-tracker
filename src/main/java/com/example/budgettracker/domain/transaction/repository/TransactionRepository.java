package com.example.budgettracker.domain.transaction.repository;

import com.example.budgettracker.domain.transaction.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserId(String userId);
} 