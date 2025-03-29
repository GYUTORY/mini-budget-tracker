package com.example.budgettracker.domain.transaction.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

/**
 * 카테고리 생성/수정 요청 DTO
 */
@Getter
@Schema(description = "카테고리 생성/수정 요청")
public class CategoryRequest {

    @Schema(description = "카테고리 이름", example = "식비")
    @NotBlank(message = "카테고리 이름은 필수입니다.")
    @Size(min = 1, max = 50, message = "카테고리 이름은 1~50자 이내여야 합니다.")
    private String name;

    @Schema(description = "카테고리 설명", example = "음식 관련 지출")
    @Size(max = 255, message = "설명은 255자 이내여야 합니다.")
    private String description;

    @Schema(description = "카테고리 아이콘", example = "food")
    @Size(max = 50, message = "아이콘은 50자 이내여야 합니다.")
    private String icon;

    @Schema(description = "카테고리 색상 (16진수 컬러 코드)", example = "#FF5733")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$", message = "올바른 색상 코드를 입력해주세요.")
    @Size(max = 7, message = "색상 코드는 7자 이내여야 합니다.")
    private String color;
} 