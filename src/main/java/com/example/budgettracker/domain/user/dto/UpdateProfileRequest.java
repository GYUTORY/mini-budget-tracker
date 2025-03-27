package com.example.budgettracker.domain.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    
    @NotBlank(message = "이름은 필수 입력값입니다.")
    private String name;
} 