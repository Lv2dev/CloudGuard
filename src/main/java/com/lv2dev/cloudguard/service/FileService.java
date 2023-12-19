package com.lv2dev.cloudguard.service;

import com.lv2dev.cloudguard.dto.DataTreeDTO;
import com.lv2dev.cloudguard.model.DataTree;
import com.lv2dev.cloudguard.persistence.DataTreeRepository;
import com.lv2dev.cloudguard.persistence.MemberRepository;
import com.lv2dev.cloudguard.utils.CustomMultipartFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
public class FileService {

    @Autowired
    private S3Service s3Service;

    @Autowired
    private EncryptionService encryptionService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private DataTreeRepository dataTreeRepository;

    // 파일 업로드 및 암호화 처리
    public void uploadAndEncryptFile(int memberId, MultipartFile file, String key, EncryptionService.EncryptionAlgorithm algorithm, int treeId) throws Exception {
        try{
            // treeId를 이용하여 DateTree 객체 가져옴
            DataTree findDataTree = dataTreeRepository.findById(treeId);

            // findDataTree의 memberId와 로그인한 memberId가 같은지 확인
            if (findDataTree != null && findDataTree.getMember().getId() != memberId) {
                throw new Exception("You do not have permission to access this folder");
            }

            // 파일의 원래 이름과 확장자를 합쳐서 임시 변수에 저장
            String originalFilename = file.getOriginalFilename();

            byte[] fileData = file.getBytes();
            byte[] encryptedData = encryptionService.encryptData(fileData, key, algorithm);

            String uuid = UUID.randomUUID().toString();
            String path = "files/" + memberId; // 예시 경로

            MultipartFile encryptedFile = new CustomMultipartFile(encryptedData, uuid + ".crypt");
            String s3Url = s3Service.uploadFile(encryptedFile, path, uuid);

            // 데이터베이스에 파일 정보 저장 로직 추가 (DataTree 테이블에 저장)
            DataTree dataTree = DataTree.builder()
                    .member(memberRepository.findById(Long.valueOf(memberId)))
                    .name(originalFilename)
                    .cryptType(algorithm.name())
                    .path(s3Url)
                    .parent((findDataTree == null ? 0 : findDataTree.getId())) // 부모 폴더가 없을 경우 0
                    .build();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public List<DataTreeDTO> getRootFolderAndFileList(int memberId) {
        // memberId를 이용해서 parent가 없는 폴더를 가져옴
        List<DataTree> rootFolderList = dataTreeRepository.findByMemberIdAndParent(memberId, 0);

        // List<DataTree>를 List<DataTreeDTO>로 변환
        return DataTreeDTO.listOf(rootFolderList);
    }
}
