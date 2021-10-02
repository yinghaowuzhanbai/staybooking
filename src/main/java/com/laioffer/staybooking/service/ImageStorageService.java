package com.laioffer.staybooking.service;

import com.google.auth.Credentials;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.laioffer.staybooking.exception.GCSUploadException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
public class ImageStorageService {
    @Value("${gcs.bucket}")
    private String bucketName;

    public String save(MultipartFile file) throws GCSUploadException { //用于和GCS交互将文件传到GCS上
        Credentials credentials = null;
        try {
            credentials = GoogleCredentials.fromStream(getClass().getClassLoader().getResourceAsStream("credentials.json"));
        } catch (IOException e) {
            throw new GCSUploadException("Failed to load GCP credentials");
        }
        Storage storage = StorageOptions.newBuilder().setCredentials(credentials).build().getService();

        String filename = UUID.randomUUID().toString();
        BlobInfo blobInfo = null;
        try {
            blobInfo = storage.createFrom(  // 上传文件
                    BlobInfo
                            .newBuilder(bucketName, filename)  // 文件配置信息 + 内容
                            .setContentType("image/jpeg")
                            .setAcl(new ArrayList<>(Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))))
                            .build(),
                    file.getInputStream()); //文件 内容
        } catch (IOException e) {
            throw new GCSUploadException("Failed to upload images to GCS");
        }

        return blobInfo.getMediaLink(); // 返回link
    }
}