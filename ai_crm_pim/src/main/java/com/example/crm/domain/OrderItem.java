package com.example.crm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    // 🔥 [포트폴리오 핵심] 모든 XToOne 관계는 무조건 LAZY(지연 로딩)로 설정! (N+1 문제 방지)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;

    private Integer orderPrice; // 주문 당시 가격 (가격은 변동될 수 있으므로 따로 저장)
    private Integer count; // 주문 수량

    // == 생성 메서드 ==
    // OrderItem이 생성될 때 자동으로 Product의 재고를 차감합니다.
    public static OrderItem createOrderItem(Product product, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.product = product;
        orderItem.orderPrice = orderPrice;
        orderItem.count = count;

        product.removeStock(count); // 재고 차감 로직 호출
        return orderItem;
    }

    // 주문 상품 금액 (주문 당시 가격 × 수량)
    public int getTotalPrice() {
        return orderPrice * count;
    }

    // 연관관계 편의 메서드를 위해 (Order 클래스에서 사용)
    protected void setOrder(Order order) {
        this.order = order;
    }
}