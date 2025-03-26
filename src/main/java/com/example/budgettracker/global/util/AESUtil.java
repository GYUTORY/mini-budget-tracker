package com.example.budgettracker.global.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 암호화/복호화를 처리하는 유틸리티 클래스
 * Spring의 @Component 어노테이션을 통해 싱글톤으로 관리됨
 */
@Component
public class AESUtil {

    /**
     * application.yml에서 설정된 AES 암호화 키를 주입받음
     * @Value 어노테이션을 통해 설정 파일의 값을 자동으로 주입
     */
    @Value("${encryption.aes.key}")
    private String secretKey;

    /**
     * AES 암호화 메서드
     * 
     * @param value 암호화할 원본 문자열
     * @return Base64로 인코딩된 암호화 문자열
     * 
     * 암호화 과정:
     * 1. SecretKeySpec을 생성하여 AES 알고리즘에 사용할 키를 준비
     * 2. Cipher 인스턴스를 생성하고 AES/ECB/PKCS5Padding 모드로 초기화
     * 3. 암호화 모드로 설정하고 키를 적용
     * 4. 입력 문자열을 바이트 배열로 변환하여 암호화
     * 5. 암호화된 바이트 배열을 Base64로 인코딩하여 문자열로 반환
     */
    public String encrypt(String value) {
        try {
            // AES 알고리즘에 사용할 키 생성 (32바이트)
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            // AES 암호화 인스턴스 생성 (ECB 모드, PKCS5Padding)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 암호화 모드로 초기화
            cipher.init(Cipher.ENCRYPT_MODE, key);
            // 입력 문자열을 암호화
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            // 암호화된 바이트 배열을 Base64 문자열로 변환
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("이름 암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * AES 복호화 메서드
     * 
     * @param encrypted Base64로 인코딩된 암호화 문자열
     * @return 복호화된 원본 문자열
     * 
     * 복호화 과정:
     * 1. SecretKeySpec을 생성하여 AES 알고리즘에 사용할 키를 준비
     * 2. Cipher 인스턴스를 생성하고 AES/ECB/PKCS5Padding 모드로 초기화
     * 3. 복호화 모드로 설정하고 키를 적용
     * 4. Base64로 인코딩된 문자열을 디코딩하여 바이트 배열로 변환
     * 5. 암호화된 바이트 배열을 복호화하여 원본 문자열로 반환
     */
    public String decrypt(String encrypted) {
        try {
            // AES 알고리즘에 사용할 키 생성 (32바이트)
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(StandardCharsets.UTF_8), "AES");
            // AES 복호화 인스턴스 생성 (ECB 모드, PKCS5Padding)
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            // 복호화 모드로 초기화
            cipher.init(Cipher.DECRYPT_MODE, key);
            // Base64 문자열을 디코딩하고 복호화
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            // 복호화된 바이트 배열을 문자열로 변환
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("이름 복호화 중 오류가 발생했습니다.", e);
        }
    }
} 