package com.akshit.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.BufferedInputStream;
import java.io.File;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String S3_BUCKET_NAME;

    public void putS3Object(String s3ObjectKey, String path){
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(S3_BUCKET_NAME)
                .key(s3ObjectKey)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(new File(path)));
    }

    public BufferedInputStream getS3Object(String s3ObjectKey){
        GetObjectRequest getObjectRequest = GetObjectRequest
                .builder()
                .bucket(S3_BUCKET_NAME)
                .key(s3ObjectKey)
                .build();
        ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
        return new BufferedInputStream(inputStream);
    }

    public void deleteS3Object(String s3ObjectKey){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest
                .builder()
                .bucket(S3_BUCKET_NAME)
                .key(s3ObjectKey)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }
}
