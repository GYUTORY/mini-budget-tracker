package com.example.budgettracker.domain.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 성공 시 클라이언트에게 전달되는 응답 DTO
 */
@Getter // Lombok: 모든 필드의 Getter 자동 생성
@NoArgsConstructor // Lombok: 파라미터 없는 기본 생성자 생성
@AllArgsConstructor // Lombok: 모든 필드를 받는 생성자 생성
@Builder // Lombok: Builder 패턴으로 객체 생성 가능하게 함
public class SignupResponse {

    private String id;        // 생성된 사용자의 고유 ID
    private String email;   // 사용자의 이메일 (중복 확인된 값)
    private String name;    // 사용자의 이름

}