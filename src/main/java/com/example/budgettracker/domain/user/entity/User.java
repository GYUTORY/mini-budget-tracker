package com.example.budgettracker.domain.user.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 사용자(User) 정보를 담는 JPA 엔티티 클래스
 */
@Entity // 이 클래스가 JPA의 엔티티임을 선언 (DB 테이블과 매핑됨)
@Table(name = "users") // DB에서 매핑될 테이블 이름 지정 (기본은 클래스명 → 여기선 "users"로 명시적 지정)
@Getter // Lombok: 모든 필드에 대한 Getter 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Lombok: 파라미터 없는 기본 생성자 자동 생성
public class User {

    @Id
    @Column(length = 36)
    private String id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(name = "created_at")
    // 생성 시각 저장 (DB 컬럼명: created_at)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    // 수정 시각 저장 (DB 컬럼명: updated_at)
    private LocalDateTime updatedAt;

    /**
     * 엔티티가 처음 저장되기 전 (INSERT 직전) 실행되는 메서드
     * → 생성일/수정일 자동 기록
     */
    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().replace("-", "");
        }
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 엔티티가 수정되기 전 (UPDATE 직전) 실행되는 메서드
     * → 수정일만 갱신
     */
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Builder
    public User(String id, String email, String password, String name) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * 사용자 이름 업데이트
     * 
     * @param name 암호화된 새로운 이름
     */
    public void updateName(String name) {
        this.name = name;
    }
}