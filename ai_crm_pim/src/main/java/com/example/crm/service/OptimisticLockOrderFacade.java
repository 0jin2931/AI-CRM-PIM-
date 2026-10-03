package com.example.crm.service;

import com.example.crm.exception.OrderRetryExceededException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class OptimisticLockOrderFacade {

    private static final int MAX_ATTEMPTS = 1000; // 무한루프 방지용 최대 재시도 횟수
    private static final long RETRY_DELAY_MS = 50;

    private final OrderService orderService;

    public Long orderWithOptimisticLock(Long memberId, Long productId, int count) {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            try {
                // 주문 시도
                return orderService.order(memberId, productId, count);

            } catch (OptimisticLockingFailureException | CannotAcquireLockException e) {
                // 🔥 [핵심]: 오직 '낙관적 락 충돌'과 'DB 데드락' 예외일 때만 재시도!
                // 재고 부족 등의 비즈니스 에러는 이 catch 블록을 타지 않고 바로 밖으로 던져집니다.
                log.info("DB 충돌 감지! 재시도합니다... ({}회 재시도)", attempt);
                sleepBeforeRetry();
            }
        }

        throw new OrderRetryExceededException("너무 많은 트래픽이 몰려 주문에 실패했습니다.");
    }

    private void sleepBeforeRetry() {
        try {
            Thread.sleep(RETRY_DELAY_MS);
        } catch (InterruptedException e) {
            // 인터럽트 상태를 복구하고 재시도를 중단
            Thread.currentThread().interrupt();
            throw new OrderRetryExceededException("주문 재시도가 중단되었습니다.");
        }
    }
}
