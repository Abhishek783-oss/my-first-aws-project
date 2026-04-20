package com.aws.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.sync.RequestBody;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
public class UploadController {

    @Autowired
    private S3Client s3Client;
    @Autowired
    private FileMetadataRepository fileMetadataRepository;
    private final String bucketName = "abhishek0591-s3-demo-bucket";

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file) throws IOException {

        String fileName = System.currentTimeMillis()+"_"+file.getOriginalFilename();

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(fileName)
                        .contentType("image/jpeg")
                        .build(),
                RequestBody.fromBytes(file.getBytes())
        );

        String fileUrl = "https://" + bucketName + ".s3.amazonaws.com/" + fileName;
        FileMetaData fileMetaData = new FileMetaData();
        fileMetaData.setFileName(fileName);
        fileMetaData.setFileUrl(fileUrl);
        fileMetaData.setUploadTime(LocalDateTime.now());
        fileMetadataRepository.save(fileMetaData);
        return fileUrl;

    }

    @GetMapping("/files")
    public List<FileMetaData> getAllFiles(){
        return fileMetadataRepository.findAll();
    }
}
