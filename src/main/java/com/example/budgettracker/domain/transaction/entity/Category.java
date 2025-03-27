package com.example.budgettracker.domain.transaction.entity;

public enum Category {
    // 수입 카테고리
    SALARY("급여"),
    BONUS("보너스"),
    INVESTMENT("투자"),
    ETC_INCOME("기타 수입"),

    // 지출 카테고리
    FOOD("식비"),
    TRANSPORTATION("교통비"),
    SHOPPING("쇼핑"),
    HOUSING("주거비"),
    UTILITIES("공과금"),
    HEALTHCARE("의료비"),
    EDUCATION("교육비"),
    ENTERTAINMENT("여가"),
    ETC_EXPENSE("기타 지출");

    private final String description;

    Category(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 