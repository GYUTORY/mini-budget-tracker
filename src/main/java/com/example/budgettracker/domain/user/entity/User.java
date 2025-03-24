package com.example.budgettracker.domain.user.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 사용자(User) 정보를 담는 JPA 엔티티 클래스
 */
@Entity // 이 클래스가 JPA의 엔티티임을 선언 (DB 테이블과 매핑됨)
@Table(name = "users") // DB에서 매핑될 테이블 이름 지정 (기본은 클래스명 → 여기선 "users"로 명시적 지정)
@Getter // Lombok: 모든 필드에 대한 Getter 자동 생성
@NoArgsConstructor // Lombok: 파라미터 없는 기본 생성자 자동 생성
@AllArgsConstructor // Lombok: 모든 필드를 받는 생성자 자동 생성
@Builder // Lombok: Builder 패턴을 통해 객체 생성 가능하게 함
public class User {

    @Id // 기본 키(PK) 지정
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    // 기본 키 자동 생성 전략: DB에서 AUTO_INCREMENT 사용
    private Long id;

    @Column(unique = true, nullable = false, length = 50) 
    // 이메일은 중복되면 안 되고(null 불가), 최대 길이 제한
    private String email;

    @Column(nullable = false)
    // 비밀번호는 필수 입력
    private String password;

    @Column(nullable = false, length = 30)
    // 사용자 이름도 필수이며, 길이 제한
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
}