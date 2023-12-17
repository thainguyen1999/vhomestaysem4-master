package com.example.vhomestay.service.impl;

import com.google.cloud.storage.*;
import com.example.vhomestay.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StorageServiceImpl implements StorageService {
    private final Storage storage;
    private final String bucketName = "hbs_bucket1";
//    private final String bucketName = "hbs_bucket";

    @Override
    public String uploadFile(MultipartFile imageFile) throws IOException {
        String fileName = UUID.randomUUID().toString(); // Tạo tên file duy nhất
        storage.create(
                BlobInfo.newBuilder(bucketName, fileName)
                        .setContentType(imageFile.getContentType())
//                        .setAcl(new ArrayList<>(
//                                Arrays.asList(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER))
//                        ))
                        .build(),
                imageFile.getInputStream()
        );
        return String.format("https://storage.googleapis.com/%s/%s", bucketName, fileName);
    }

    @Override
    public String getFile(String fileName) {
        String blobName = fileName.substring(fileName.lastIndexOf("/") + 1);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        if (blob != null) {
            return blob.getMediaLink();
        }
        return null;
    }

    @Override
    public String updateFile(String fileName, MultipartFile imageFile) throws IOException {
        // Kiểm tra xem ảnh cũ có tồn tại hay không
        if(fileName != null && !fileName.equals("")) {
            String blobName = fileName.substring(fileName.lastIndexOf("/") + 1);
            BlobId oldBlobId = BlobId.of(bucketName, blobName);
            Blob oldBlob = storage.get(oldBlobId);

            // Nếu ảnh cũ tồn tại, xoá ảnh cũ trước khi tạo đối tượng Blob mới
            if (oldBlob != null) {
                storage.delete(oldBlobId);
                return uploadFile(imageFile);
            } else {
                return null;
            }
        }
        return uploadFile(imageFile);
    }

    @Override
    public void deleteFile(String fileName) {
        String blobName = fileName.substring(fileName.lastIndexOf("/") + 1);
        BlobId blobId = BlobId.of(bucketName, blobName);
        Blob blob = storage.get(blobId);
        if (blob == null) {
            return;
        }
        storage.delete(blobId);
    }

}
