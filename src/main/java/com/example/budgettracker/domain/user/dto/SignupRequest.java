package com.example.budgettracker.domain.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원가입 요청 데이터를 담는 DTO
 * 클라이언트 → 서버로 전달될 때 사용됨
 */
@Getter // 모든 필드의 Getter 메서드를 자동 생성
@NoArgsConstructor // 파라미터 없는 기본 생성자 생성 (JSON 역직렬화 시 필요)
@AllArgsConstructor // 모든 필드를 인자로 받는 생성자 자동 생성
public class SignupRequest {

    @NotBlank(message = "이메일은 필수 입력값입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    // 이메일 필드는 공백이 아니어야 하고, 형식이 올바른 이메일이어야 함
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력값입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    // 비밀번호는 공백 불가, 최소 8자 이상
    private String password;

    @NotBlank(message = "이름은 필수 입력값입니다.")
    @Size(max = 30, message = "이름은 30자를 초과할 수 없습니다.")
    // 이름은 공백 불가, 30자 이하로 제한
    private String name;
}