package com.konggogi.veganlife.global.util.file;


import java.util.Arrays;
import java.util.List;

public class ImageFileExtensionValidator implements FileExtensionValidator {

    @Override
    public boolean isValid(String originalFilename) {
        String fileExtension =
                originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        List<String> allowedExtensions = Arrays.asList("jpg", "png", "jpeg", "webp");

        return allowedExtensions.contains(fileExtension);
    }
}
