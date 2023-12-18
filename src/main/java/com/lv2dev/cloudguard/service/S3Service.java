package com.lv2dev.cloudguard.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class S3Service {

    private final AmazonS3 s3Client;

    @Autowired
    public S3Service(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    // S3 버킷 이름
    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    public String uploadFile(String jsonFileData, String path, String keyName) {
        // JSON 문자열에서 파일 데이터 추출
        JSONObject jsonObject = new JSONObject(jsonFileData);
        String fileContent = jsonObject.getString("fileContent"); // 파일 내용을 나타내는 키
        String fileExtension = jsonObject.optString("fileExtension", ""); // 파일 확장자 (옵셔널)

        byte[] data = fileContent.getBytes(); // 파일 내용을 바이트 배열로 변환

        // 바이트 배열을 이용하여 ByteArrayInputStream 생성
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

        // S3에 파일 업로드
        String fullKeyName = path + "/" + keyName + (fileExtension.isEmpty() ? "" : "." + fileExtension);
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(data.length);
        PutObjectRequest request = new PutObjectRequest(bucketName, fullKeyName, byteArrayInputStream, metadata);
        s3Client.putObject(request);

        // 업로드된 파일의 URL 반환
        return s3Client.getUrl(bucketName, fullKeyName).toString();
    }
}
