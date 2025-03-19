package com.example.chatrepo.file;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

//@Service
//public class CloudFileUploadService {
//
//    private final Storage storage = StorageOptions.getDefaultInstance().getService();
//
//    @Value("${gcp.bucket.name}")
//    private String bucketName;
//
//    private String makeFolder() {
//        return "IMAGE/" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
//    }
//
//    public String uploadImg(MultipartFile file) {
//        validateFile(file);
//
//        String uploadPath = makeFolder();
//        String fileName = uploadPath + "/" + UUID.randomUUID();
//
//        try {
//            BlobInfo blobInfo = BlobInfo.newBuilder(bucketName, fileName)
//                    .setContentType(file.getContentType())
//                    .build();
//            storage.create(blobInfo, file.getInputStream());
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to upload file", e);
//        }
//
//        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
//    }
//
//    private void validateFile(MultipartFile file) {
//        if (file == null || file.isEmpty()) {
//            throw new IllegalArgumentException("Invalid file");
//        }
//    }
//}
