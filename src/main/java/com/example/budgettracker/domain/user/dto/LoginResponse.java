package com.example.budgettracker.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private String token;
    private String userId;
    private String email;
    private String name;
} 