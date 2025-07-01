package com.example.finalproject.exception.error;

/**
 * 인증 실패 시 발생하는 예외입니다.
 * 비밀번호 불일치, 인증 토큰 만료 등 인증 관련 오류에 사용됩니다.
 */
public class UnAuthorizedException extends RuntimeException {
    
    public UnAuthorizedException(String message) {
        super(message);
    }
    
    public UnAuthorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
