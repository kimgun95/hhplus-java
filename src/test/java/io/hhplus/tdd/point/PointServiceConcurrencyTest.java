package io.hhplus.tdd.point;

import io.hhplus.tdd.constant.Id;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PointServiceConcurrencyTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserPointRepository userPointRepository;

    @Test
    public void 똑같은UserPoint에동시에여러충전요청이들어올때_모든포인트가정상충전됨() throws InterruptedException {
        // Given
        Id<UserPoint, Long> userId = new Id<>(UserPoint.class, 1L);  // 테스트할 사용자 ID
        long initialAmount = 0L;
        int concurrentThreads = 10;  // 동시에 10개의 스레드가 포인트 충전
        long chargeAmount = 1000L;   // 각각의 요청에서 1,000 포인트를 충전

        // 사용자 초기화
        userPointRepository.saveOrUpdate(userId.value(), initialAmount);

        // ExecutorService를 사용해 멀티 스레드 환경 구성
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch latch = new CountDownLatch(concurrentThreads);  // 모든 스레드의 종료를 기다리기 위한 latch

        // 동시 충전 시도
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    pointService.chargeUserPoint(userId, chargeAmount);  // 포인트 충전 요청
                } finally {
                    latch.countDown();  // 스레드 완료 시 latch 카운트 감소
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);  // 모든 스레드가 완료될 때까지 기다림
        executorService.shutdown();

        // Then: 최종 포인트가 올바르게 충전되었는지 검증
        UserPoint finalUserPoint = userPointRepository.findById(userId.value());
        long expectedPoints = chargeAmount * concurrentThreads;  // 기대되는 최종 포인트 (10 * 1000 = 10000)
        assertEquals(expectedPoints, finalUserPoint.point(), "총 충전된 포인트가 예상과 같습니다.");
    }

    @Test
    public void 똑같은UserPoint에동시에여러사용요청이들어올때_모든포인트가정상이용됨() throws InterruptedException {
        // Given
        Id<UserPoint, Long> userId = new Id<>(UserPoint.class, 1L);  // 테스트할 사용자 ID
        long initialAmount = 10_000L;  // 초기 포인트는 10,000
        int concurrentThreads = 10;  // 동시에 10개의 스레드가 포인트 사용
        long useAmount = 1_000L;   // 각각의 요청에서 1,000 포인트 사용

        // 사용자 초기화
        userPointRepository.saveOrUpdate(userId.value(), initialAmount);

        // ExecutorService를 사용해 멀티 스레드 환경 구성
        ExecutorService executorService = Executors.newFixedThreadPool(concurrentThreads);
        CountDownLatch latch = new CountDownLatch(concurrentThreads);  // 모든 스레드의 종료를 기다리기 위한 latch

        // 동시 사용 시도
        for (int i = 0; i < concurrentThreads; i++) {
            executorService.submit(() -> {
                try {
                    pointService.useUserPoint(userId, useAmount);  // 포인트 사용 요청
                } finally {
                    latch.countDown();  // 스레드 완료 시 latch 카운트 감소
                }
            });
        }

        latch.await(10, TimeUnit.SECONDS);  // 모든 스레드가 완료될 때까지 기다림
        executorService.shutdown();

        // Then: 최종 포인트가 올바르게 사용되었는지 검증
        UserPoint finalUserPoint = userPointRepository.findById(userId.value());
        long expectedPoints = initialAmount - (useAmount * concurrentThreads);  // 기대되는 최종 포인트 (10,000 - 10 * 1000 = 0)
        assertEquals(expectedPoints, finalUserPoint.point(), "총 사용된 포인트가 예상과 같습니다.");
    }


}

