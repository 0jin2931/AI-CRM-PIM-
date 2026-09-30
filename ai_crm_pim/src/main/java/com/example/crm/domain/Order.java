package com.example.crm.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders") // order는 SQL 예약어인 경우가 많아 테이블명을 orders로 지정
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // CascadeType.ALL: Order를 저장할 때 포함된 OrderItem들도 함께 자동 저장됨
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems = new ArrayList<>();

    private String status; // ORDERED, CANCELED

    private LocalDateTime orderDate;

    // == 연관관계 편의 메서드 ==
    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    // == 생성 메서드 ==
    public static Order createOrder(Member member, OrderItem... orderItems) {
        Order order = new Order();
        order.member = member;
        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.status = "ORDERED";
        order.orderDate = LocalDateTime.now();
        return order;
    }
}