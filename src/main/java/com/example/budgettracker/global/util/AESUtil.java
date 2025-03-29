package com.example.budgettracker.global.util;

import org.springframework.beans.factory.annotation.Value;
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
    private static final String ALGORITHM = "AES";

    /**
     * AESUtil 생성자
     * 
     * @param secret 암호화 키
     */
    public AESUtil(@Value("${encryption.aes.key}") String secret) {
        // 암호화 키를 16바이트로 맞추기 위해 패딩
        byte[] key = secret.getBytes(StandardCharsets.UTF_8);
        byte[] paddedKey = new byte[16];
        System.arraycopy(key, 0, paddedKey, 0, Math.min(key.length, paddedKey.length));
        this.secretKey = new SecretKeySpec(paddedKey, ALGORITHM);
    }

    /**
     * 문자열을 AES로 암호화
     * 
     * @param data 암호화할 문자열
     * @return 암호화된 문자열 (Base64 인코딩)
     */
    public String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new RuntimeException("암호화 중 오류가 발생했습니다.", e);
        }
    }

    /**
     * 암호화된 문자열을 AES로 복호화
     * 
     * @param encryptedData 암호화된 문자열 (Base64 인코딩)
     * @return 복호화된 문자열
     */
    public String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("복호화 중 오류가 발생했습니다.", e);
        }
    }
} 