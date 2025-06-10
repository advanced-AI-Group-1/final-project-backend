package com.example.finalproject.exception.error;

import lombok.Getter;

public class PdfGenerationException extends RuntimeException {
    private ErrorType errorType;

    public enum ErrorType {
        PDF_GENERATION_FAILED,
        PDF_FILE_NOT_FOUND
    }

    public ErrorType getErrorType() {
        return errorType;
    }

    public PdfGenerationException(ErrorType errorType) {
        super();
        this.errorType = errorType;
    }
}
