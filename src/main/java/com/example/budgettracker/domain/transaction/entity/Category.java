package com.example.budgettracker.domain.transaction.entity;

import com.example.budgettracker.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 거래 카테고리 엔티티
 * 
 * @Entity: JPA 엔티티 클래스임을 나타냄
 * @Table: 데이터베이스 테이블 정보를 지정
 * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
 * @NoArgsConstructor: 기본 생성자 자동 생성
 * @EntityListeners: 엔티티 이벤트 리스너 설정
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Category {

    /**
     * 카테고리 ID (PK)
     * 
     * @Id: 기본키 지정
     * @GeneratedValue: ID 자동 생성 전략 설정
     * @Column: 컬럼 정보 설정
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    /**
     * 카테고리 이름
     * 
     * @Column: 
     *   - nullable = false: NULL 불가
     *   - length = 50: 최대 길이
     */
    @Column(nullable = false, length = 50)
    private String name;

    /**
     * 카테고리 설명
     * 
     * @Column: 
     *   - length = 255: 최대 길이
     */
    @Column(length = 255)
    private String description;

    /**
     * 카테고리 아이콘
     * 
     * @Column: length = 50으로 최대 길이 설정
     */
    @Column(length = 50)
    private String icon;

    /**
     * 카테고리 색상
     * 
     * @Column: length = 7로 최대 길이 설정 (예: #FFFFFF)
     */
    @Column(length = 7)
    private String color;

    /**
     * 사용자 ID
     * 
     * @Column: 
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * 기본 카테고리 여부
     * 
     * @Column: 
     *   - nullable = false: NULL 불가
     *   - columnDefinition = "BOOLEAN DEFAULT false": 기본값 false
     */
    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isDefault = false;

    /**
     * 생성일시
     * 
     * @CreatedDate: 생성일시 자동 기록
     */
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    /**
     * 수정일시
     * 
     * @LastModifiedDate: 수정일시 자동 기록
     */
    @LastModifiedDate
    private LocalDateTime updatedAt;

    /**
     * 카테고리 엔티티 생성자
     * 
     * @Builder: 빌더 패턴 구현
     */
    @Builder
    public Category(String name, String description, String icon, String color, Long userId, Boolean isDefault) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
        this.userId = userId;
        this.isDefault = isDefault != null ? isDefault : false;
    }

    /**
     * 카테고리 정보 업데이트
     * 
     * @param name 새로운 카테고리 이름
     * @param description 새로운 카테고리 설명
     * @param icon 새로운 카테고리 아이콘
     * @param color 새로운 카테고리 색상
     */
    public void update(String name, String description, String icon, String color) {
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.color = color;
    }
} 