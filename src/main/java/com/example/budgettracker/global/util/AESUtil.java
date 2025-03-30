package com.example.budgettracker.global.util;

import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * AES 암호화/복호화를 위한 유틸리티 클래스
 * 
 * @Component: Spring 컴포넌트로 등록
 */
@Component
public class AESUtil {

    private final SecretKeySpec secretKey;

    /**
     * AESUtil 생성자
     * 
     * @param secret 암호화 키
     */
    public AESUtil(String secret) {
        this.secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "AES");
    }

    /**
     * 문자열을 AES로 암호화
     * 
     * @param value 암호화할 문자열
     * @return 암호화된 문자열 (Base64 인코딩)
     */
    public String encrypt(String value) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(value.getBytes());
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 암호화된 문자열을 AES로 복호화
     * 
     * @param encrypted 암호화된 문자열 (Base64 인코딩)
     * @return 복호화된 문자열
     */
    public String decrypt(String encrypted) {
        try {
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encrypted));
            return new String(decryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("복호화 중 오류가 발생했습니다.", e);
        }
    }
} 