package com.example.budgettracker.domain.transaction.repository;

import com.example.budgettracker.domain.transaction.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 카테고리 엔티티를 위한 데이터 액세스 계층
 * 
 * @Repository: 데이터 액세스 계층임을 나타내는 Spring 스테레오타입 어노테이션
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    /**
     * 카테고리 이름으로 카테고리 찾기
     * 
     * @param name 찾을 카테고리 이름
     * @return 카테고리 (Optional)
     */
    Optional<Category> findByName(String name);
    
    /**
     * 특정 사용자의 모든 카테고리 찾기
     * 
     * @param userId 사용자 ID
     * @return 사용자의 카테고리 목록
     */
    List<Category> findByUserId(Long userId);
    
    /**
     * 기본 카테고리 여부로 카테고리 찾기
     * 
     * @param isDefault 기본 카테고리 여부
     * @return 기본 카테고리 목록
     */
    List<Category> findByIsDefaultTrue();
} 