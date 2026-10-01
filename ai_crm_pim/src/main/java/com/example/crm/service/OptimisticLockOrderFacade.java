package com.example.crm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
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
                // 주문 시도
                return orderService.order(memberId, productId, count);
                
            } catch (OptimisticLockingFailureException | CannotAcquireLockException e) {
                // 🔥 [핵심]: 오직 '낙관적 락 충돌'과 'DB 데드락' 예외일 때만 재시도!
                // 재고 부족 등의 비즈니스 에러는 이 catch 블록을 타지 않고 바로 밖으로 던져집니다.
                attempt++;
                log.info("DB 충돌 감지! 재시도합니다... ({}회 재시도)", attempt);
                Thread.sleep(50);
            }
        }
        
        throw new RuntimeException("너무 많은 트래픽이 몰려 주문에 실패했습니다.");
    }
}