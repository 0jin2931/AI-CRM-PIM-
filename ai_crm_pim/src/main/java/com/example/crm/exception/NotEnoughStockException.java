package com.example.crm.exception; // 패키지명은 본인 프로젝트에 맞게 수정하세요

public class NotEnoughStockException extends RuntimeException {
    public NotEnoughStockException(String message) {
        super(message);
    }
}