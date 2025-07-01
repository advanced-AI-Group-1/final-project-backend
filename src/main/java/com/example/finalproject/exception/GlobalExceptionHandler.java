package com.example.finalproject.exception;

import com.example.finalproject.exception.error.AIServerUnavailableException;
import com.example.finalproject.exception.error.DuplicateUserException;
import com.example.finalproject.exception.error.FinancialDataParseException;
import com.example.finalproject.exception.error.PdfGenerationException;
import com.example.finalproject.exception.error.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
/**
 * 전역 예외 처리 핸들러 클래스
 *
 * Spring Boot 애플리케이션에서 발생하는 모든 예외를 중앙에서 처리하며,
 * 각 예외 타입에 따라 적절한 HTTP 상태 코드와 에러 메시지를 반환합니다.
 *
 * 처리하는 예외 타입별 HTTP 상태 코드:
 * - AIServerUnavailableException:
 *   handleAIError() → GATEWAY_TIMEOUT (504) - AI 서버와의 통신에서 응답이 없거나 시간 초과가 발생한 경우
 *
 * - PdfGenerationException:
 *   handlePdfError() → 에러 타입에 따라 다른 HTTP 상태 코드 반환
 *   * PDF_GENERATION_FAILED: INTERNAL_SERVER_ERROR (500) - 서버 내부 오류로 PDF 생성 실패
 *   * PDF_FILE_NOT_FOUND: NOT_FOUND (404) - 요청한 PDF 파일을 찾을 수 없음
 *
 * - FinancialDataParseException:
 *   handleParsingError() → BAD_REQUEST (400) - 클라이언트에서 전송한 재무 데이터 형식이 올바르지 않은 경우
 *
 * - Exception (기타 모든 예외):
 *   handleDefault() → INTERNAL_SERVER_ERROR (500) - 위에서 정의되지 않은 모든 예외를 처리하는 폴백 핸들러
 *
 * @RestControllerAdvice를 사용하여 모든 컨트롤러에서 발생하는 예외를 처리
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AIServerUnavailableException.class)
    public ResponseEntity<String> handleAIError(AIServerUnavailableException e) {
        return ResponseEntity.status(HttpStatus.GATEWAY_TIMEOUT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("AI 서버 응답 없음: " + e.getMessage());
    }

    @ExceptionHandler(PdfGenerationException.class)
    public ResponseEntity<String> handlePdfError(PdfGenerationException e) {
        if (e.getErrorType() == PdfGenerationException.ErrorType.PDF_GENERATION_FAILED) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("PDF 생성 실패");
        }
        if (e.getErrorType() == PdfGenerationException.ErrorType.PDF_FILE_NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("PDF 파일을 찾을 수 없습니다");
        }

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("알 수 없는 PDF 오류: " + e.getMessage());
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

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateUserException.class)
    public ResponseEntity<ApiResponse<String>> handleDuplicateUser(DuplicateUserException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ApiResponse.error(ex.getMessage()));
    }
}
