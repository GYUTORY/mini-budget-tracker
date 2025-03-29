package com.example.budgettracker.domain.transaction.dto;

import com.example.budgettracker.domain.transaction.entity.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

/**
 * 카테고리 응답 DTO
 */
@Getter
@Builder
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

    @Schema(description = "기본 카테고리 여부", example = "true")
    private Boolean isDefault;

    /**
     * 카테고리 엔티티를 응답 DTO로 변환
     * 
     * @param category 카테고리 엔티티
     * @return 카테고리 응답 DTO
     */
    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .icon(category.getIcon())
                .color(category.getColor())
                .isDefault(category.getIsDefault())
                .build();
    }
} 