package com.example.chatrepo.file;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.chatrepo.common.BaseResponseStatus;
import com.example.chatrepo.exception.custom.InvalidFileException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CloudFileUploadService {
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Value("{cloud.aws.region.static")
    private String region;

    private static final List<String> IMAGE_EXTENSIONS = Arrays.asList("png", "jpg","jpeg","gif","webp","bmp","svg");
    private static final Long MAX_FILE_SIZE = 10*1024*1024L;

    private String makeFolder(){
        return "IMAGE/"+ LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
    }
    private void validateFile(MultipartFile file){
        if(file==null || file.isEmpty()){
            throw new InvalidFileException(BaseResponseStatus.EXCEED_MAX_SIZE);
        }
        validateFileType(file);
    }
    private static void validateFileType(MultipartFile file){
        String fileContentType = file.getContentType();
        if(fileContentType==null||!fileContentType.startsWith("image/")){
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYLE);
        }
        String fileName = file.getOriginalFilename();
        if(fileName==null|| fileName.isEmpty()) {
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYLE);
        }
        int dotIndex = fileName.lastIndexOf(".");
        if(dotIndex==-1){
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYLE);
        }
        String fileExtension = fileName.substring(dotIndex+1).toLowerCase();
        if(!IMAGE_EXTENSIONS.contains(fileExtension)){
            throw new InvalidFileException(BaseResponseStatus.INVALID_FILE_TYLE);
        }
    }
    public String uploadImg(MultipartFile file){
        validateFile(file);

        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());

        String uploadPath = makeFolder();
        String fileName = uploadPath+"/"+ UUID.randomUUID();

        try {
            amazonS3Client.putObject(bucketName, fileName, file.getInputStream(), objectMetadata);
        } catch(IOException e){
            throw new InvalidFileException(BaseResponseStatus.FAIL);
        }
        return amazonS3Client.getUrl(bucketName, fileName).toString();
    }

}
