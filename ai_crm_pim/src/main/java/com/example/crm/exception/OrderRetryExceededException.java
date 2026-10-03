package com.example.crm.exception;

// 낙관적 락 충돌로 재시도 횟수를 초과했을 때 발생
public class OrderRetryExceededException extends RuntimeException {
    public OrderRetryExceededException(String message) {
        super(message);
    }
}
