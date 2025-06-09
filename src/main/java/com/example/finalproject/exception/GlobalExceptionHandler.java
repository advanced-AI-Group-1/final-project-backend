package com.example.finalproject.exception;

import com.example.finalproject.exception.error.AIServerUnavailableException;
import com.example.finalproject.exception.error.FinancialDataParseException;
import com.example.finalproject.exception.error.PdfGenerationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AIServerUnavailableException.class)
    public ResponseEntity<String> handleAIError(AIServerUnavailableException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .body("AI 서버 응답 없음: " + e.getMessage());
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<String> handlePdfError(PdfGenerationException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("PDF 생성 실패: " + e.getMessage());
    }

    @ExceptionHandler(FinancialDataParseException.class)
    public ResponseEntity<String> handleParsingError(FinancialDataParseException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("재무데이터 파싱 실패: " + e.getMessage());
    }

    // 기타 예상치 못한 예외
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleDefault(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("서버 내부 오류: " + e.getMessage());
    }
}
