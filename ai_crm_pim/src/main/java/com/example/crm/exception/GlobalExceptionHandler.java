package com.example.crm.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * 모든 API 에러를 {status: "FAIL", message: ...} 형태로 통일해서 반환
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 재고 부족, 존재하지 않는 회원/상품 등 비즈니스 예외
    @ExceptionHandler({NotEnoughStockException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException e) {
        return fail(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    // 요청 값 검증 실패 (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .orElse("잘못된 요청입니다.");
        return fail(HttpStatus.BAD_REQUEST, message);
    }

    // JSON 형식 오류, 숫자 필드에 문자열 입력 등
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleNotReadable(HttpMessageNotReadableException e) {
        return fail(HttpStatus.BAD_REQUEST, "요청 본문 형식이 올바르지 않습니다.");
    }

    // Gemini 호출/파싱 실패
    @ExceptionHandler(AiResponseException.class)
    public ResponseEntity<Map<String, Object>> handleAi(AiResponseException e) {
        log.warn("AI 처리 실패: {}", e.getMessage(), e);
        return fail(HttpStatus.BAD_GATEWAY, e.getMessage());
    }

    // 낙관적 락 재시도 초과
    @ExceptionHandler(OrderRetryExceededException.class)
    public ResponseEntity<Map<String, Object>> handleRetryExceeded(OrderRetryExceededException e) {
        return fail(HttpStatus.SERVICE_UNAVAILABLE, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception e) {
        log.error("예상치 못한 서버 에러", e);
        return fail(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.");
    }

    private ResponseEntity<Map<String, Object>> fail(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of("status", "FAIL", "message", message));
    }
}
