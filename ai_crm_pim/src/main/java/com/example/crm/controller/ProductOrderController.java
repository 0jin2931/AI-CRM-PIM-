package com.example.crm.controller;

import com.example.crm.domain.Product;
import com.example.crm.repository.ProductRepository;
import com.example.crm.service.OptimisticLockOrderFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductOrderController {

    private final ProductRepository productRepository;
    private final OptimisticLockOrderFacade optimisticLockOrderFacade;

    // 상품 전체 조회
    @GetMapping("/products")
    public ResponseEntity<List<Product>> getProducts() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    // 상품 등록/초기화 (테스트용)
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        return ResponseEntity.ok(productRepository.save(product));
    }

    // 낙관적 락 주문 실행 (동시성 제어 테스트 연동)
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> placeOrder(@RequestBody Map<String, Object> req) {
        Long memberId = Long.valueOf(req.get("memberId").toString());
        Long productId = Long.valueOf(req.get("productId").toString());
        int count = Integer.parseInt(req.get("count").toString());

        try {
            Long orderId = optimisticLockOrderFacade.orderWithOptimisticLock(memberId, productId, count);
            return ResponseEntity.ok(Map.of("status", "SUCCESS", "orderId", orderId));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("status", "FAIL", "message", e.getMessage()));
        }
    }
}