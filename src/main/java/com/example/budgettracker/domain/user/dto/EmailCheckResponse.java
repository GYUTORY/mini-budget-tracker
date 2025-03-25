package com.example.budgettracker.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EmailCheckResponse {
    private boolean isAvailable;
    private String message;
} 