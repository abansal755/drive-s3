package com.akshit.api.utils;

import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class FileIOUtils {
    public static void createDirectoryHierarchyIfNotExists(String path) throws IOException {
        Files.createDirectories(Paths.get(path));
    }

    public static void deleteFileIfExists(String path) throws IOException {
        Files.deleteIfExists(Paths.get(path));
    }

    public static void downloadStreamToFile(BufferedInputStream inputStream, String path) throws IOException {
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
        int b;
        while((b = inputStream.read()) != -1)
            outputStream.write(b);
        outputStream.flush();
        outputStream.close();
    }

    public static void putS3Object(S3Client s3Client, String s3BucketName, String s3ObjectKey, String path){
        PutObjectRequest putObjectRequest = PutObjectRequest
                .builder()
                .bucket(s3BucketName)
                .key(s3ObjectKey)
                .build();
        s3Client.putObject(putObjectRequest, RequestBody.fromFile(new File(path)));
    }

    public static BufferedInputStream getS3Object(S3Client s3Client, String s3BucketName, String s3ObjectKey){
        GetObjectRequest getObjectRequest = GetObjectRequest
                    .builder()
                    .bucket(s3BucketName)
                    .key(s3ObjectKey)
                    .build();
        ResponseInputStream<GetObjectResponse> inputStream = s3Client.getObject(getObjectRequest);
        return new BufferedInputStream(inputStream);
    }
}