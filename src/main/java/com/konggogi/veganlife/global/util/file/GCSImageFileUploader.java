package com.konggogi.veganlife.global.util.file;


import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.FileUploadException;
import com.konggogi.veganlife.global.util.file.domain.Directory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class GCSImageFileUploader implements FileUploader {

    private final Storage storage;
    private final FileExtensionValidator fileExtensionValidator;

    @Value("${spring.cloud.gcp.storage.bucket.name}")
    private String bucket;

    @Value("${spring.cloud.gcp.storage.bucket.host}")
    private String host;

    public GCSImageFileUploader(Storage storage) {
        this.storage = storage;
        this.fileExtensionValidator = new ImageFileExtensionValidator();
    }

    @Override
    public List<String> uploadFiles(Directory directory, List<MultipartFile> multipartFiles) {
        if (multipartFiles == null) {
            return Collections.emptyList();
        }
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            fileUrls.add(uploadFile(directory, multipartFile));
        }
        return fileUrls;
    }

    @Override
    public String uploadFile(Directory directory, MultipartFile multipartFile) {
        if (multipartFile == null) {
            return null;
        }
        String newFileName = directory.getName() + generateRandomFilename(multipartFile);

        BlobInfo blobInfo =
                BlobInfo.newBuilder(bucket, newFileName)
                        .setContentType(multipartFile.getContentType())
                        .build();
        try {
            storage.createFrom(blobInfo, multipartFile.getInputStream());
        } catch (IOException e) {
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return String.format("%s/%s/%s", host, bucket, newFileName);
    }

    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new FileUploadException(ErrorCode.NULL_FILE_NAME);
        }
        if (fileExtensionValidator.isValid(originalFileName)) {
            throw new FileUploadException(ErrorCode.INVALID_EXTENSION);
        }
        return UUID.randomUUID() + ".webp";
    }
}
