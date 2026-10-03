package com.example.crm.controller;

import com.example.crm.domain.Product;
import com.example.crm.dto.OrderRequest;
import com.example.crm.dto.ProductCreateRequest;
import com.example.crm.dto.ProductResponse;
import com.example.crm.repository.ProductRepository;
import com.example.crm.service.OptimisticLockOrderFacade;
import jakarta.validation.Valid;
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
    public ResponseEntity<List<ProductResponse>> getProducts() {
        return ResponseEntity.ok(productRepository.findAll().stream()
                .map(ProductResponse::from)
                .toList());
    }

    // 상품 등록 (테스트용)
    @PostMapping("/products")
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductCreateRequest request) {
        Product saved = productRepository.save(request.toEntity());
        return ResponseEntity.ok(ProductResponse.from(saved));
    }

    // 낙관적 락 주문 실행 (동시성 제어 테스트 연동)
    // 실패 응답은 GlobalExceptionHandler가 {status: "FAIL", message} 형태로 반환
    @PostMapping("/orders")
    public ResponseEntity<Map<String, Object>> placeOrder(@Valid @RequestBody OrderRequest req) {
        Long orderId = optimisticLockOrderFacade.orderWithOptimisticLock(req.memberId(), req.productId(), req.count());
        return ResponseEntity.ok(Map.of("status", "SUCCESS", "orderId", orderId));
    }
}
