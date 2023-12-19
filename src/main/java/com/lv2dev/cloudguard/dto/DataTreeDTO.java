package com.lv2dev.cloudguard.dto;

import com.lv2dev.cloudguard.model.DataTree;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataTreeDTO {
    private Long id;

    private Long memberId;

    private String cryptType; // folder인 경우 폴더임, null 이거나 암호화 알고리즘인 경우 파일임

    private String path; // s3 url

    private String name; // 파일 또는 폴더 이름

    private Long parent; // 해당 파일 또는 폴더가 저장된 경로

    public static List<DataTreeDTO> listOf(List<DataTree> dataTreeList) {
        return dataTreeList.stream().map(dataTree -> DataTreeDTO.builder()
                        .id(dataTree.getId())
                        .memberId(dataTree.getMember() != null ? dataTree.getMember().getId() : null)
                        .cryptType(dataTree.getCryptType())
                        .path(dataTree.getPath())
                        .parent(dataTree.getParent())
                        .build())
                .collect(Collectors.toList());
    }
}
