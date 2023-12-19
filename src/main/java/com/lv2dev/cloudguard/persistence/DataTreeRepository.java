package com.lv2dev.cloudguard.persistence;

import com.lv2dev.cloudguard.model.DataTree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface DataTreeRepository extends JpaRepository<DataTree, Long> {
    /**
     * 아이디로 찾기
     * */
    DataTree findById(long id);

    /**
     * memberId, parent로 찾기
     * */
    List<DataTree> findByMemberIdAndParent(long memberId, long parent);
}
