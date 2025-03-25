package com.example.budgettracker.global.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean result;
    private String code;
    private T resultSet;
    private String msg;

    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .result(true)
                .code("200")
                .resultSet(data)
                .msg(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .result(false)
                .code(code)
                .msg(message)
                .build();
    }

    public static <T> ApiResponse<T> error(String code, String message, T resultSet) {
        return ApiResponse.<T>builder()
                .result(false)
                .code(code)
                .resultSet(resultSet)
                .msg(message)
                .build();
    }
} 