package com.example.crm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.example.crm.exception.NotEnoughStockException;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String category; // 예: "CPU", "RAM", "GPU"

    @Column(nullable = false)
    private String name; // 예: "AMD Ryzen 7 7800X3D"

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer stockQuantity; // 재고 수량

    // 🔥 [포트폴리오 핵심 포인트] 낙관적 락(Optimistic Lock)을 위한 버전 필드
    // 동시에 여러 사용자가 구매할 때 재고 정합성을 맞추기 위해 사용됩니다.
    @Version
    private Long version;

    @Builder
    public Product(String category, String name, Integer price, Integer stockQuantity) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    // 재고 차감 비즈니스 로직
    public void removeStock(int quantity) {
        int restStock = this.stockQuantity - quantity;
        if (restStock < 0) {
            // 커스텀 예외 처리 (나중에 생성할 예정)
            throw new NotEnoughStockException("재고가 부족합니다.");
        }
        this.stockQuantity = restStock;
    }
}