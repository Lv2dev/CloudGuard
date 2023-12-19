package com.lv2dev.cloudguard.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "data_tree")
public class DataTree {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Member 를 join
    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(name = "crypt_type")
    private String cryptType;

    @Column(name = "path")
    private String path; // s3 url

    @Column(name = "name")
    private String name; // 파일 또는 폴더 이름


    @Column(name = "parent")
    private Long parent; // 해당 파일 또는 폴더가 저장된 경로
}
