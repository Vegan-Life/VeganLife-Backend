package com.konggogi.veganlife.global;


import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.konggogi.veganlife.global.domain.AwsS3Folders;
import com.konggogi.veganlife.global.exception.ErrorCode;
import com.konggogi.veganlife.global.exception.FileUploadException;
import com.sksamuel.scrimage.ImmutableImage;
import com.sksamuel.scrimage.webp.WebpWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
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
        if (multipartFiles == null) {
            return Collections.emptyList();
        }
        List<String> fileUrls = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            fileUrls.add(uploadFile(uploadFolder, multipartFile));
        }
        return fileUrls;
    }

    public String uploadFile(AwsS3Folders uploadFolder, MultipartFile multipartFile) {
        if (multipartFile == null) {
            return null;
        }
        String newFileName = uploadFolder.getName() + generateRandomFilename(multipartFile);
        File file = convertMultipartFileToFile(multipartFile);
        File webpFile = convertFileToWebp(file);

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(webpFile.length());
        metadata.setContentType("image/webp");

        try (FileInputStream fileInputStream = new FileInputStream(webpFile)) {
            amazonS3.putObject(bucket, newFileName, fileInputStream, metadata);
        } catch (SdkClientException | IOException e) {
            throw new FileUploadException(ErrorCode.FILE_UPLOAD_ERROR);
        } finally {
            deleteTempFiles(file, webpFile);
        }
        return cloudfrontDomain + newFileName;
    }

    private String generateRandomFilename(MultipartFile multipartFile) {
        String originalFileName = multipartFile.getOriginalFilename();
        if (originalFileName == null) {
            throw new FileUploadException(ErrorCode.NULL_FILE_NAME);
        }
        validateFileExtension(originalFileName);
        return UUID.randomUUID() + ".webp";
    }

    private void validateFileExtension(String originalFilename) {
        String fileExtension =
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg", "webp");

        if (!allowedExtensions.contains(fileExtension)) {
            throw new FileUploadException(ErrorCode.INVALID_EXTENSION);
        }
    }

    private File convertMultipartFileToFile(MultipartFile multipartFile) {
        File file = new File(multipartFile.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(multipartFile.getBytes());
            return file;
        } catch (IOException e) {
            throw new FileUploadException(ErrorCode.FILE_CONVERT_ERROR);
        }
    }

    private File convertFileToWebp(File file) {
        File outputFile = new File("resize_" + System.currentTimeMillis() + ".webp");
        try {
            return ImmutableImage.loader().fromFile(file).output(WebpWriter.DEFAULT, outputFile);
        } catch (IOException e) {
            throw new FileUploadException(ErrorCode.FILE_CONVERT_ERROR);
        }
    }

    private void deleteTempFiles(File... files) {
        for (File file : files) {
            try {
                Files.deleteIfExists(file.toPath());
            } catch (IOException e) {
                throw new FileUploadException(ErrorCode.FILE_DELETE_ERROR);
            }
        }
    }
}
