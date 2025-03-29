package com.example.budgettracker.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT 토큰 생성 및 검증을 위한 유틸리티 클래스
 * 
 * @Component: Spring 컴포넌트로 등록
 */
@Component
public class JwtUtil {

    private final Key key;
    private final long jwtExpiration;

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration}") long jwtExpiration) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    /**
     * JWT 토큰에서 사용자 이메일 추출
     * 
     * @param token JWT 토큰
     * @return 사용자 이메일
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * JWT 토큰에서 만료일 추출
     * 
     * @param token JWT 토큰
     * @return 만료일
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * JWT 토큰에서 특정 클레임 추출
     * 
     * @param token JWT 토큰
     * @param claimsResolver 클레임 추출 함수
     * @return 추출된 클레임
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * JWT 토큰 생성
     * 
     * @param userDetails 사용자 정보
     * @return 생성된 JWT 토큰
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    /**
     * JWT 토큰 생성
     * 
     * @param username 사용자 이메일
     * @return 생성된 JWT 토큰
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, username);
    }

    /**
     * JWT 토큰 생성
     * 
     * @param claims 클레임 정보
     * @param subject 토큰 주체
     * @return 생성된 JWT 토큰
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration * 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @param userDetails 사용자 정보
     * @return 토큰 유효성 여부
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * JWT 토큰 유효성 검증
     * 
     * @param token JWT 토큰
     * @param username 사용자 이메일
     * @return 토큰 유효성 여부
     */
    public boolean isTokenValid(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * JWT 토큰 만료 여부 확인
     * 
     * @param token JWT 토큰
     * @return 만료 여부
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * JWT 토큰에서 모든 클레임 추출
     * 
     * @param token JWT 토큰
     * @return 모든 클레임
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String getUserIdFromToken(String token) {
        return extractUsername(token);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
} 