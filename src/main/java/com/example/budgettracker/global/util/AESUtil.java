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

    private final SecretKeySpec secretKey;

    public AESUtil(@Value("${aes.secret}") String secret) {
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        this.secretKey = new SecretKeySpec(key, "AES");
    }

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
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to encrypt value", e);
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
    public String decrypt(String encryptedValue) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedValue));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("Failed to decrypt value", e);
        }
    }
} 