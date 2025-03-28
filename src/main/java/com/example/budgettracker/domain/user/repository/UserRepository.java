package com.example.budgettracker.domain.user.repository;

import com.example.budgettracker.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 엔티티(User)에 대한 DB 접근을 담당하는 리포지토리 인터페이스
 */
@Repository // Spring이 이 인터페이스를 Bean으로 인식하도록 명시 (사실 생략해도 됨)
public interface UserRepository extends JpaRepository<User, String> {
    /**
     * 이메일을 기준으로 사용자 조회
     * @param email - 사용자 이메일
     * @return Optional<User> - 결과가 없을 수도 있으므로 Optional로 반환
     */
    Optional<User> findByEmail(String email);

    /**
     * 해당 이메일을 가진 사용자가 존재하는지 여부 확인
     * @param email - 사용자 이메일
     * @return 존재 여부 (true / false)
     */
    boolean existsByEmail(String email);
}