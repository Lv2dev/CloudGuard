package com.lv2dev.cloudguard.persistence;

import com.lv2dev.cloudguard.model.Member;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;


public interface MemberRepository extends ElasticsearchRepository<Member, Integer> {
    /**
     * 이메일로 찾기
     * */
    Member findByEmail(String email);
}
