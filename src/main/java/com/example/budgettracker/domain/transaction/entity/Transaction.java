package com.example.budgettracker.domain.transaction.entity;

import com.example.budgettracker.domain.transaction.dto.TransactionRequest;
import com.example.budgettracker.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 거래 내역 엔티티
 * 
 * @Entity: JPA 엔티티 클래스임을 나타냄
 * @Table: 데이터베이스 테이블 정보를 지정
 * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
 * @NoArgsConstructor: 기본 생성자 자동 생성
 * @EntityListeners: 엔티티 이벤트 리스너 설정
 */
@Entity
@Table(name = "transactions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Transaction {

    /**
     * 거래 ID (PK)
     * 
     * @Id: 기본키 지정
     * @GeneratedValue: ID 자동 생성 전략 설정
     * @Column: 컬럼 정보 설정
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 거래 금액
     * 
     * @Column: 
     *   - nullable = false: NULL 불가
     *   - precision = 10: 전체 자릿수
     *   - scale = 2: 소수점 자릿수
     */
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    /**
     * 거래 설명
     * 
     * @Column: 
     *   - nullable = false: NULL 불가
     *   - length = 255: 최대 길이
     */
    @Column(nullable = false, length = 255)
    private String description;

    /**
     * 거래 유형
     * 
     * @Enumerated: Enum 타입 매핑 설정
     * @Column: nullable = false로 NULL 불가 설정
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    /**
     * 거래 카테고리
     * 
     * @ManyToOne: N:1 관계 설정
     *   - fetch = FetchType.LAZY: 지연 로딩
     *   - optional = false: NULL 불가
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;

    /**
     * 거래 사용자
     * 
     * @ManyToOne: N:1 관계 설정
     *   - fetch = FetchType.LAZY: 지연 로딩
     *   - optional = false: NULL 불가
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    /**
     * 거래 일시
     * 
     * @Column: nullable = false로 NULL 불가 설정
     */
    @Column(nullable = false)
    private LocalDateTime date;

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

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 거래 내역 엔티티 생성자
     * 
     * @Builder: 빌더 패턴 구현
     */
    @Builder
    public Transaction(User user, TransactionType type, Category category, BigDecimal amount,
                      LocalDateTime date, String description) {
        this.user = user;
        this.type = type;
        this.category = category;
        this.amount = amount;
        this.date = date;
        this.description = description;
    }

    /**
     * 거래 사용자 설정
     * 
     * @param user 설정할 사용자
     */
    public void setUser(User user) {
        this.user = user;
    }

    /**
     * 거래 금액 업데이트
     * 
     * @param amount 새로운 거래 금액
     */
    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /**
     * 거래 설명 업데이트
     * 
     * @param description 새로운 거래 설명
     */
    public void updateDescription(String description) {
        this.description = description;
    }

    /**
     * 거래 카테고리 업데이트
     * 
     * @param category 새로운 거래 카테고리
     */
    public void updateCategory(Category category) {
        this.category = category;
    }

    /**
     * 거래 일시 업데이트
     * 
     * @param date 새로운 거래 일시
     */
    public void updateDate(LocalDateTime date) {
        this.date = date;
    }

    public void update(TransactionRequest request) {
        this.amount = request.getAmount();
        this.type = request.getType();
        this.category = request.getCategory();
        this.description = request.getDescription();
        this.date = request.getDate();
    }
} 