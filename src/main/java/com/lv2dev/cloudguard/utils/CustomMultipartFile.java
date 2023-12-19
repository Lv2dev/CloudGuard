package com.lv2dev.cloudguard.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class CustomMultipartFile implements MultipartFile {
    private final byte[] fileContent;
    private final String fileName;

    public CustomMultipartFile(byte[] fileContent, String fileName) {
        this.fileContent = fileContent;
        this.fileName = fileName;
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public String getOriginalFilename() {
        return fileName;
    }

    @Override
    public String getContentType() {
        return "application/octet-stream";
    }

    @Override
    public boolean isEmpty() {
        return fileContent == null || fileContent.length == 0;
    }

    @Override
    public long getSize() {
        return fileContent.length;
    }

    @Override
    public byte[] getBytes() {
        return fileContent;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(fileContent);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        try (InputStream in = getInputStream();
             FileOutputStream out = new FileOutputStream(dest)) {
            int read;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
        }
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        InputStream inputStream = getInputStream();
        java.nio.file.Files.copy(inputStream, dest, StandardCopyOption.REPLACE_EXISTING);
        inputStream.close();
    }


    // ... 나머지 MultipartFile 메서드 구현 ...
}

