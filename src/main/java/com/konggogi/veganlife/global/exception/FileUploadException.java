package com.konggogi.veganlife.global.exception;

public class FileUploadException extends ApiException {

    public FileUploadException(ErrorCode errorCode) {
        super(errorCode);
    }

    public FileUploadException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
