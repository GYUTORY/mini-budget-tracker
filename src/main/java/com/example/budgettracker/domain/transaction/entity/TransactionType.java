package com.example.budgettracker.domain.transaction.entity;

/**
 * 거래 유형을 나타내는 열거형
 * 
 * INCOME: 수입 유형의 거래
 * EXPENSE: 지출 유형의 거래
 */
public enum TransactionType {
    /**
     * 수입 유형의 거래
     * - 급여, 사업 수입, 투자 수익 등
     */
    INCOME,

    /**
     * 지출 유형의 거래
     * - 식비, 교통비, 주거비 등
     */
    EXPENSE
} 