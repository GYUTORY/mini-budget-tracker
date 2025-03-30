package com.example.budgettracker.domain.user.entity;

import com.example.budgettracker.domain.transaction.entity.Transaction;
import com.example.budgettracker.global.util.AESUtil;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 사용자 엔티티
 * 
 * @Entity: JPA 엔티티 클래스임을 나타냄
 * @Table: 데이터베이스 테이블 정보를 지정
 * @Getter: Lombok을 사용하여 getter 메서드 자동 생성
 * @NoArgsConstructor: 기본 생성자 자동 생성
 * @EntityListeners: 엔티티 이벤트 리스너 설정
 */
@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class User {

    /**
     * 사용자 ID (PK)
     * 
     * @Id: 기본키 지정
     * @GeneratedValue: ID 자동 생성 전략 설정
     * @Column: 컬럼 정보 설정
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    /**
     * 사용자 이메일
     * 
     * @Column: 
     *   - unique = true: 유니크 제약조건
     *   - nullable = false: NULL 불가
     */
    @Column(nullable = false, unique = true)
    private String email;

    /**
     * 사용자 비밀번호
     * 
     * @Column: nullable = false로 NULL 불가 설정
     */
    @Column(nullable = false)
    private String password;

    /**
     * 사용자 이름 (암호화)
     * 
     * @Column: nullable = false로 NULL 불가 설정
     */
    @Column(nullable = false)
    private String name;

    /**
     * 사용자의 거래 내역 목록
     * 
     * @OneToMany: 1:N 관계 설정
     *   - mappedBy: 연관관계의 주인 필드 지정
     *   - cascade: 영속성 전이 설정
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<Transaction> transactions = new ArrayList<>();

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
     * 사용자 엔티티 생성자
     * 
     * @Builder: 빌더 패턴 구현
     */
    @Builder
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    /**
     * 사용자 이름 암호화
     * 
     * @param aesUtil AES 암호화 유틸리티
     */
    public void encryptName(AESUtil aesUtil) {
        this.name = aesUtil.encrypt(this.name);
    }

    /**
     * 사용자 이름 복호화
     * 
     * @param aesUtil AES 암호화 유틸리티
     * @return 복호화된 사용자 이름
     */
    public String decryptName(AESUtil aesUtil) {
        return aesUtil.decrypt(this.name);
    }

    /**
     * 거래 내역 추가
     * 
     * @param transaction 추가할 거래 내역
     */
    public void addTransaction(Transaction transaction) {
        this.transactions.add(transaction);
        transaction.setUser(this);
    }

    public void updateName(String name) {
        this.name = name;
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}