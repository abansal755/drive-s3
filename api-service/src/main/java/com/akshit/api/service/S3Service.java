package com.akshit.api.service;

import com.akshit.api.exception.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;

@Service
public class S3Service {

    @Autowired
    private S3Client s3Client;

    @Value("${s3.bucket-name}")
    private String S3_BUCKET_NAME;

    public void putS3Object(String s3ObjectKey, InputStream inputStream, long contentLength){
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(S3_BUCKET_NAME)
                .key(s3ObjectKey)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(inputStream, contentLength));
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

    public Long getS3ObjectSize(String s3ObjectKey){
        GetObjectAttributesRequest request = GetObjectAttributesRequest
                .builder()
                .bucket(S3_BUCKET_NAME)
                .key(s3ObjectKey)
                .objectAttributes(ObjectAttributes.OBJECT_SIZE)
                .build();
        return s3Client.getObjectAttributes(request).objectSize();
    }
}
