package com.konggogi.veganlife.global.util.file;


import com.konggogi.veganlife.global.util.file.domain.Directory;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface FileUploader {

    List<String> uploadFiles(Directory directory, List<MultipartFile> multipartFiles);

    String uploadFile(Directory directory, MultipartFile multipartFile);
}
