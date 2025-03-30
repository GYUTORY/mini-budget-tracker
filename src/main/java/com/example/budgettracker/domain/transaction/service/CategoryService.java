package com.example.budgettracker.domain.transaction.service;

import com.example.budgettracker.domain.transaction.dto.CategoryRequest;
import com.example.budgettracker.domain.transaction.dto.CategoryResponse;
import com.example.budgettracker.domain.transaction.entity.Category;
import com.example.budgettracker.domain.transaction.repository.CategoryRepository;
import com.example.budgettracker.domain.user.entity.User;
import com.example.budgettracker.domain.user.repository.UserRepository;
import com.example.budgettracker.global.exception.CustomException;
import com.example.budgettracker.global.exception.ErrorCode;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 카테고리 관리 서비스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    /**
     * 기본 카테고리 초기화
     * 애플리케이션 시작 시 기본 카테고리가 없으면 생성
     */
    @PostConstruct
    @Transactional
    public void initDefaultCategories() {
        if (categoryRepository.findByIsDefaultTrue().isEmpty()) {
            List<Category> defaultCategories = Arrays.asList(
                new Category("식비", "음식 관련 지출", "food", "#FF5733", null, true),
                new Category("교통비", "교통 수단 관련 지출", "transport", "#33A8FF", null, true),
                new Category("주거비", "주택 관련 고정 지출", "home", "#33FF57", null, true),
                new Category("쇼핑", "의류, 생필품 등 구매", "shopping", "#A833FF", null, true),
                new Category("문화생활", "영화, 공연, 취미 활동 등", "entertainment", "#FF33A8", null, true),
                new Category("월급", "정기적인 급여 수입", "salary", "#33FFA8", null, true),
                new Category("용돈", "가족, 친구 등에게 받은 용돈", "gift", "#FFA833", null, true)
            );
            
            categoryRepository.saveAll(defaultCategories);
        }
    }

    /**
     * 모든 카테고리 조회 (기본 + 사용자 정의)
     *
     * @param userId 사용자 ID
     * @return 카테고리 목록
     */
    public List<CategoryResponse> getAllCategories(String userId) {
        List<Category> defaultCategories = categoryRepository.findByIsDefaultTrue();
        List<Category> userCategories = categoryRepository.findByUserId(Long.parseLong(userId));
        
        // 두 목록 합치기
        defaultCategories.addAll(userCategories);
        
        return defaultCategories.stream()
                .map(CategoryResponse::from)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리 상세 조회
     *
     * @param userId 사용자 ID
     * @param categoryId 카테고리 ID
     * @return 카테고리 상세 정보
     */
    public CategoryResponse getCategory(String userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // 기본 카테고리가 아니고 다른 사용자의 카테고리인 경우 접근 거부
        if (!category.getIsDefault() && 
            category.getUserId() != null && 
            !category.getUserId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        return CategoryResponse.from(category);
    }

    /**
     * 사용자 정의 카테고리 생성
     *
     * @param userId 사용자 ID
     * @param request 카테고리 생성 요청
     * @return 생성된 카테고리
     */
    @Transactional
    public CategoryResponse createCategory(String userId, CategoryRequest request) {
        // 사용자 존재 여부 확인
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }
        
        // 이미 동일한 이름의 카테고리가 있는지 확인 (기본 카테고리 포함)
        if (categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .description(request.getDescription())
                .icon(request.getIcon())
                .color(request.getColor())
                .userId(Long.parseLong(userId))
                .isDefault(false)
                .build();
        
        category = categoryRepository.save(category);
        return CategoryResponse.from(category);
    }

    /**
     * 사용자 정의 카테고리 수정
     *
     * @param userId 사용자 ID
     * @param categoryId 카테고리 ID
     * @param request 카테고리 수정 요청
     * @return 수정된 카테고리
     */
    @Transactional
    public CategoryResponse updateCategory(String userId, Long categoryId, CategoryRequest request) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // 기본 카테고리는 수정 불가
        if (category.getIsDefault()) {
            throw new CustomException(ErrorCode.CANNOT_MODIFY_DEFAULT_CATEGORY);
        }
        
        // 다른 사용자의 카테고리는 수정 불가
        if (!category.getUserId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        // 다른 이름의 카테고리로 변경 시 이름 중복 체크
        if (!category.getName().equals(request.getName()) && 
            categoryRepository.findByName(request.getName()).isPresent()) {
            throw new CustomException(ErrorCode.CATEGORY_ALREADY_EXISTS);
        }
        
        category.update(request.getName(), request.getDescription(), request.getIcon(), request.getColor());
        return CategoryResponse.from(category);
    }

    /**
     * 사용자 정의 카테고리 삭제
     *
     * @param userId 사용자 ID
     * @param categoryId 카테고리 ID
     */
    @Transactional
    public void deleteCategory(String userId, Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
        
        // 기본 카테고리는 삭제 불가
        if (category.getIsDefault()) {
            throw new CustomException(ErrorCode.CANNOT_DELETE_DEFAULT_CATEGORY);
        }
        
        // 다른 사용자의 카테고리는 삭제 불가
        if (!category.getUserId().toString().equals(userId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        
        categoryRepository.delete(category);
    }
} 