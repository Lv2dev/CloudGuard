package com.lv2dev.cloudguard.persistence;

import com.lv2dev.cloudguard.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Integer> {
    /**
     * 이메일로 찾기
     * */
    Member findByEmail(String email);

    /**
     * existsByEmail
     * */
    boolean existsByEmail(String email);

    /**
     * 닉네임 중복 체크
     * */
    boolean existsByNickname(String nickname); // 닉네임 중복 체크를 위한 메소드

}
