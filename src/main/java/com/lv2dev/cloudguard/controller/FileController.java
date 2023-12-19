package com.lv2dev.cloudguard.controller;

import com.lv2dev.cloudguard.service.FileService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.lv2dev.cloudguard.service.EncryptionService;
import springfox.documentation.annotations.ApiIgnore;

import java.io.IOException;

@Api(tags = "파일 저장 및 폴더 생성 컨트롤러")
@Slf4j
@RestController
@RequestMapping("/api/file")
@Controller
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@ApiIgnore Authentication authentication,
                                        @RequestParam("file") MultipartFile file,
                                        @RequestParam("encryptionKey") String key, // 암호화 할때 필요한 키
                                        @RequestParam("algorithm") EncryptionService.EncryptionAlgorithm algorithm, // 알고리즘 종류
                                        @RequestParam("treeId") int treeId // 연결된 date tree id(폴더 아이디)
    ) {
        try {
            int memberId = Integer.parseInt(authentication.getPrincipal().toString());
            fileService.uploadAndEncryptFile(memberId, file, key, algorithm, treeId);
            return ResponseEntity.ok().body("File uploaded successfully.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("File upload failed");
        }
    }

    /**
     * 회원의 root 폴더와 파일을 List로 반환하는 API
     * */
    @GetMapping("/list/root")
    public ResponseEntity<?> getRootFolderAndFileList(@ApiIgnore Authentication authentication) {
        int memberId = Integer.parseInt(authentication.getPrincipal().toString());
        return ResponseEntity.ok().body(fileService.getRootFolderAndFileList(memberId));
    }
}
