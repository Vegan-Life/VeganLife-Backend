package com.konggogi.veganlife.global.exception;

public class ElasticsearchOperationException extends ApiException {
    public ElasticsearchOperationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ElasticsearchOperationException(ErrorCode errorCode, String description) {
        super(errorCode, description);
    }
}
