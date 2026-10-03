package com.example.crm.exception;

// Gemini API 호출 실패 또는 응답 파싱 실패 시 발생
public class AiResponseException extends RuntimeException {
    public AiResponseException(String message) {
        super(message);
    }

    public AiResponseException(String message, Throwable cause) {
        super(message, cause);
    }
}
