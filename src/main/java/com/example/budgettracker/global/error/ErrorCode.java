package com.example.budgettracker.global.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    // User
    USER_NOT_FOUND("User not found"),
    USER_ALREADY_EXISTS("User already exists"),
    INVALID_PASSWORD("Invalid password"),

    // Transaction
    TRANSACTION_NOT_FOUND("Transaction not found"),
    INVALID_TRANSACTION_TYPE("Invalid transaction type"),
    INVALID_CATEGORY("Invalid category"),

    // Common
    INVALID_REQUEST("Invalid request"),
    INTERNAL_SERVER_ERROR("Internal server error");

    private final String message;
} 