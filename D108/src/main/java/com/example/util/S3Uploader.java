package com.example.util;

import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;

@Component
public class S3Uploader {

    private final S3Client s3Client;
    private final String bucketName = "gumi-d-108";

    // 생성자 주입
    public S3Uploader(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * byte[] 파일 데이터를 S3에 업로드하는 메서드
     * @param fileBytes   업로드할 파일 바이트 배열
     * @param fileName    S3 상에 저장할 파일명
     * @param contentType 파일 MIME 타입
     * @return 업로드된 파일의 public URL
     */
    public String uploadFile(byte[] fileBytes, String fileName, String contentType) throws IOException {
        String key = "images/" + fileName; // S3 내부 경로(key)

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentType(contentType)
                .acl("public-read") // 해당 파일을 public-read로 설정(선택사항)
                .build();

        // putObject 호출
        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

        // public-read일 경우 접근 URL 예시
        return "https://" + bucketName + ".s3.ap-northeast-2.amazonaws.com/d108/" + key;
    }
}
