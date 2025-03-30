package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "카테고리 응답")
public class CategoryResponse {

    @Schema(description = "카테고리 ID", example = "1")
    private Long id;

    @Schema(description = "카테고리 이름", example = "식비")
    private String name;

    @Schema(description = "카테고리 설명", example = "음식 관련 지출")
    private String description;

    @Schema(description = "카테고리 아이콘", example = "food")
    private String icon;

    @Schema(description = "카테고리 색상", example = "#FF5733")
    private String color;

    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .color(category.getColor())
                .userId(category.getUserId())
                .build();
    }
} 