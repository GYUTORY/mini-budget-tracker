package com.example.budgettracker.global.util

import spock.lang.Specification
import spock.lang.Subject

class AESUtilTest extends Specification {

    @Subject
    AESUtil aesUtil = new AESUtil()

    def "이름 암호화 및 복호화 성공"() {
        given:
        def originalName = "홍길동"

        when:
        def encrypted = aesUtil.encrypt(originalName)
        def decrypted = aesUtil.decrypt(encrypted)

        then:
        encrypted != null
        encrypted != originalName
        decrypted == originalName
    }

    def "빈 문자열 암호화 및 복호화"() {
        given:
        def originalName = ""

        when:
        def encrypted = aesUtil.encrypt(originalName)
        def decrypted = aesUtil.decrypt(encrypted)

        then:
        encrypted != null
        encrypted != originalName
        decrypted == originalName
    }

    def "특수문자 포함 이름 암호화 및 복호화"() {
        given:
        def originalName = "홍길동!@#$%^&*()"

        when:
        def encrypted = aesUtil.encrypt(originalName)
        def decrypted = aesUtil.decrypt(encrypted)

        then:
        encrypted != null
        encrypted != originalName
        decrypted == originalName
    }

    def "긴 이름 암호화 및 복호화"() {
        given:
        def originalName = "홍길동홍길동홍길동홍길동홍길동홍길동홍길동홍길동"

        when:
        def encrypted = aesUtil.encrypt(originalName)
        def decrypted = aesUtil.decrypt(encrypted)

        then:
        encrypted != null
        encrypted != originalName
        decrypted == originalName
    }
} 