package com.example.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockOrderFacade {

    private final OrderService orderService;

    public Long orderWithOptimisticLock(Long memberId, Long productId, int count) throws InterruptedException {
        int attempt = 0; // 무한루프 방지용 카운트
        
        while (attempt < 1000) { 
            try {
                return orderService.order(memberId, productId, count);
            } catch (Exception e) {
                // "재고가 부족합니다" 같은 진짜 비즈니스 로직 에러는 재시도하면 안 됨! 바로 던짐.
                if (e instanceof IllegalArgumentException) {
                    log.error("주문 실패 (비즈니스 에러): {}", e.getMessage());
                    throw e;
                }
                
                // 그 외 낙관적 락 예외, MySQL 데드락 예외 등 모든 충돌 에러는 50ms 대기 후 재시도!
                attempt++;
                log.info("DB 충돌 감지! 재시도합니다... ({}회 재시도)", attempt);
                Thread.sleep(50);
            }
        }
        throw new RuntimeException("너무 많은 트래픽이 몰려 주문에 실패했습니다.");
    }
}