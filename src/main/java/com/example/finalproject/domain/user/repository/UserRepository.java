package com.example.finalproject.domain.user.repository;

import com.example.finalproject.domain.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * 사용자(UserEntity) 데이터 접근을 위한 리포지토리 인터페이스입니다.
 * JPA를 사용하여 사용자 정보를 데이터베이스에서 조회하고 관리합니다.
 *
 * <p>주요 기능:
 * <ul>
 *   <li>사용자 ID로 사용자 조회</li>
 *   <li>사용자 ID와 비밀번호로 사용자 조회 (로그인용)</li>
 *   <li>기본 CRUD 기능 (JpaRepository 상속)</li>
 * </ul>
 *
 * <p>사용 예시:
 * <pre>
 * // 사용자 ID로 조회
 * Optional<UserEntity> user = userRepository.findByUserId("test123");
 *
 * // 로그인 시 사용자 인증
 * Optional<UserEntity> user = userRepository.findByUserIdAndPassword("test123", encodedPassword);
 * </pre>
 *
 * @see com.example.finalproject.domain.user.entity.UserEntity
 */
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);
}
