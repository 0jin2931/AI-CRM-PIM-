package com.example.crm.service;

import com.example.crm.domain.Member;
import com.example.crm.domain.Product;
import com.example.crm.repository.MemberRepository;
import com.example.crm.repository.OrderRepository; // 🔥 추가
import com.example.crm.repository.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class OrderConcurrencyTest {

    @Autowired
    private OptimisticLockOrderFacade optimisticLockOrderFacade;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private OrderRepository orderRepository; // 🔥 추가: 주문 내역 삭제를 위해 주입

    private Member testMember;
    private Product testProduct;

    @BeforeEach
    public void before() {
        // 🔥 혹시 남아있을지 모르는 이전 찌꺼기 데이터 먼저 삭제 (순서 중요: 주문 -> 상품/회원)
        orderRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();

        // 🔥 이메일 중복 에러를 방지하기 위해 현재 시간을 이메일에 붙여서 무조건 유니크하게 생성
        testMember = memberRepository.save(Member.builder()
                .name("테스터")
                .email("test_" + System.currentTimeMillis() + "@test.com") 
                .grade("NORMAL")
                .build());

        testProduct = productRepository.save(Product.builder()
                .category("CPU")
                .name("AMD Ryzen 7 7800X3D")
                .price(500000)
                .stockQuantity(100) // 초기 재고 100개
                .build());
    }

    @AfterEach
    public void after() {
        // 🔥 삭제 순서: 자식 테이블(Order) 먼저 지우고 부모 테이블(Product, Member)을 지워야 외래키 에러가 안 납니다!
        orderRepository.deleteAll();
        productRepository.deleteAll();
        memberRepository.deleteAll();
    }
    @Test
    @DisplayName("100명의 요청이 동시에 들어왔을 때 낙관적 락으로 재고가 정상 차감되어야 한다")
    public void 동시에_100명_주문_동시성_테스트() throws InterruptedException {
        int threadCount = 100;
        
        // 🔥 변경점: HikariCP 기본 커넥션 수(10개)에 맞춰 스레드 풀을 10개로 안정화!
        // 100명이 10개의 창구에서 질서정연하게 주문을 뚫고 들어갑니다.
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    optimisticLockOrderFacade.orderWithOptimisticLock(testMember.getId(), testProduct.getId(), 1);
                } catch (Exception e) {
                    System.out.println("🚨 찐 스레드 에러: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Product findProduct = productRepository.findById(testProduct.getId()).orElseThrow();

        System.out.println("======================================");
        System.out.println("테스트 종료 후 남은 재고: " + findProduct.getStockQuantity() + "개");
        System.out.println("======================================");
        
        assertThat(findProduct.getStockQuantity()).isEqualTo(0);
    }

    @Test
    @DisplayName("100명의 요청이 동시에 들어왔을 때 비관적 락으로 재고가 정상 차감되어야 한다")
    public void 동시에_100명_주문_비관적_락_테스트() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(10);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.execute(() -> {
                try {
                    orderService.orderWithPessimisticLock(testMember.getId(), testProduct.getId(), 1);
                } catch (Exception e) {
                    System.out.println("🚨 찐 스레드 에러: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Product findProduct = productRepository.findById(testProduct.getId()).orElseThrow();
        assertThat(findProduct.getStockQuantity()).isEqualTo(0);
    }
}