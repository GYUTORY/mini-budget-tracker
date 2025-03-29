package com.example.budgettracker.domain.transaction.controller;

import com.example.budgettracker.domain.transaction.dto.CategoryRequest;
import com.example.budgettracker.domain.transaction.dto.CategoryResponse;
import com.example.budgettracker.domain.transaction.service.CategoryService;
import com.example.budgettracker.global.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 카테고리 관련 API 컨트롤러
 * 
 * @Tag: Swagger 문서에서 API 그룹을 나타냄
 * @RestController: REST API 컨트롤러임을 나타냄
 * @RequestMapping: 기본 URL 경로 설정
 * @RequiredArgsConstructor: final 필드에 대한 생성자 자동 생성
 */
@Tag(name = "카테고리", description = "카테고리 관리 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 모든 카테고리 조회 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @return 모든 카테고리 목록
     */
    @Operation(summary = "카테고리 목록 조회", description = "사용자가 사용할 수 있는 모든 카테고리(기본 + 사용자 정의)를 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategories(
            @Parameter(hidden = true)
            Authentication authentication) {
        String userId = authentication.getName();
        return ResponseEntity.ok(categoryService.getAllCategories(userId));
    }

    /**
     * 카테고리 상세 조회 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @param id 카테고리 ID
     * @return 카테고리 상세 정보
     */
    @Operation(summary = "카테고리 상세 조회", description = "특정 카테고리의 상세 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponse> getCategory(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable Long id) {
        String userId = authentication.getName();
        return ResponseEntity.ok(categoryService.getCategory(userId, id));
    }

    /**
     * 카테고리 생성 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @param request 카테고리 생성 요청 데이터
     * @return 생성된 카테고리 응답 데이터
     */
    @Operation(summary = "카테고리 생성", description = "새로운 사용자 정의 카테고리를 생성합니다.")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "카테고리 정보", required = true)
            @Valid @RequestBody CategoryRequest request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(categoryService.createCategory(userId, request));
    }

    /**
     * 카테고리 수정 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @param id 카테고리 ID
     * @param request 카테고리 수정 요청 데이터
     * @return 수정된 카테고리 응답 데이터
     */
    @Operation(summary = "카테고리 수정", description = "기존의 사용자 정의 카테고리를 수정합니다. 기본 카테고리는 수정할 수 없습니다.")
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponse> updateCategory(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "수정할 카테고리 정보", required = true)
            @Valid @RequestBody CategoryRequest request) {
        String userId = authentication.getName();
        return ResponseEntity.ok(categoryService.updateCategory(userId, id, request));
    }

    /**
     * 카테고리 삭제 API
     * 
     * @Operation: API 엔드포인트 설명
     * @param authentication 인증 정보
     * @param id 카테고리 ID
     * @return 삭제 성공 응답
     */
    @Operation(summary = "카테고리 삭제", description = "사용자 정의 카테고리를 삭제합니다. 기본 카테고리는 삭제할 수 없습니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(
            @Parameter(hidden = true)
            Authentication authentication,
            @Parameter(description = "카테고리 ID", required = true)
            @PathVariable Long id) {
        String userId = authentication.getName();
        categoryService.deleteCategory(userId, id);
        return ResponseEntity.ok().build();
    }
} 