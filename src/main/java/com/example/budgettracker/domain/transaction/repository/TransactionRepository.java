package com.example.budgettracker.domain.transaction.repository;

import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, String> {
    List<Transaction> findByUserOrderByTransactionDateDesc(User user);
} 