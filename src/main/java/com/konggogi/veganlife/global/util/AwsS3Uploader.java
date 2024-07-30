package com.konggogi.veganlife.global.util;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.FileUploadException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Component
public final class AwsS3Uploader {

    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Value("${cloud.aws.cloudfront.domain}")
    private String cloudfrontDomain;

    public List<String> uploadFiles(AwsS3Folders uploadFolder, List<MultipartFile> multipartFiles) {
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            fileUrls.add(uploadFile(uploadFolder, multipartFile));
        }
        return fileUrls;
    }

    public String uploadFile(AwsS3Folders uploadFolder, MultipartFile multipartFile) {

        String newFileName = uploadFolder.getName() + generateRandomFilename(multipartFile);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        try {
            amazonS3.putObject(bucket, newFileName, multipartFile.getInputStream(), metadata);
        } catch (SdkClientException | IOException e) {
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_ERROR);
        }
        return cloudfrontDomain + newFileName;
    }

    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new FileUploadException(ErrorCode.NULL_FILE_NAME);
        }
        String extension = validateFileExtension(originalFileName);
        return UUID.randomUUID() + "." + extension;
    }

    private String validateFileExtension(String originalFilename) {
        String fileExtension =
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "gif", "jpeg");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new FileUploadException(ErrorCode.INVALID_EXTENSION);
        }
        return fileExtension;
    }
}
