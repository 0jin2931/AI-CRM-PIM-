package com.example.crm.service;

import com.example.crm.domain.Member;
import com.example.crm.domain.Order;
import com.example.crm.domain.OrderItem;
import com.example.crm.domain.Product;
import com.example.crm.repository.MemberRepository;
import com.example.crm.repository.OrderRepository;
import com.example.crm.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    /**
     * 상품 주문
     * @Transactional: 메서드 내의 모든 DB 작업이 하나의 단위로 묶임. (실패 시 자동 롤백)
     */
    @Transactional
    public Long order(Long memberId, Long productId, int count) {
        // 1. 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 상품입니다."));

        // 2. 주문 상품 생성 (이 과정에서 Product.removeStock()이 호출되어 재고가 차감됨)
        OrderItem orderItem = OrderItem.createOrderItem(product, product.getPrice(), count);

        // 3. 주문 생성
        Order order = Order.createOrder(member, orderItem);

        // 4. 주문 저장 (CascadeType.ALL 설정 덕분에 orderItem도 자동으로 함께 DB에 Insert 됨)
        orderRepository.save(order);
        
        return order.getId();
    }
}