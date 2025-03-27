package com.example.budgettracker.global.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import spock.lang.Specification
import spock.lang.Subject

import java.util.Date

class JwtUtilTest extends Specification {

    @Subject
    JwtUtil jwtUtil = new JwtUtil()

    def "토큰 생성 성공"() {
        given:
        def userId = "test-user-id"

        when:
        def token = jwtUtil.generateToken(userId)

        then:
        token != null
        token.split("\\.").length == 3  // JWT 토큰은 3개의 부분으로 구성됨
    }

    def "토큰 검증 성공"() {
        given:
        def userId = "test-user-id"
        def token = jwtUtil.generateToken(userId)

        when:
        def isValid = jwtUtil.validateToken(token)

        then:
        isValid
    }

    def "토큰에서 사용자 ID 추출 성공"() {
        given:
        def userId = "test-user-id"
        def token = jwtUtil.generateToken(userId)

        when:
        def extractedUserId = jwtUtil.getUserIdFromToken(token)

        then:
        extractedUserId == userId
    }

    def "잘못된 토큰 검증 실패"() {
        given:
        def invalidToken = "invalid.token.string"

        when:
        def isValid = jwtUtil.validateToken(invalidToken)

        then:
        !isValid
    }

    def "만료된 토큰 검증 실패"() {
        given:
        def userId = "test-user-id"
        def token = generateExpiredToken(userId)

        when:
        def isValid = jwtUtil.validateToken(token)

        then:
        !isValid
    }

    private String generateExpiredToken(String userId) {
        def claims = Jwts.claims().setSubject(userId)
        def now = System.currentTimeMillis()
        def expiredDate = now - 1000  // 1초 전에 만료된 토큰

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(expiredDate))
                .signWith(jwtUtil.getSigningKey())
                .compact()
    }
} 