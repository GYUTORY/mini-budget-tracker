package com.example.budgettracker.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // Common
    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    // Auth
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.BAD_REQUEST, "잘못된 비밀번호입니다."),
    UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "권한이 없습니다."),

    // Transaction
    TRANSACTION_NOT_FOUND(HttpStatus.NOT_FOUND, "거래 내역을 찾을 수 없습니다."),
    
    // Category
    CATEGORY_NOT_FOUND(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다."),
    CATEGORY_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 카테고리입니다."),
    CANNOT_MODIFY_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST, "기본 카테고리는 수정할 수 없습니다."),
    CANNOT_DELETE_DEFAULT_CATEGORY(HttpStatus.BAD_REQUEST, "기본 카테고리는 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String message;
} 