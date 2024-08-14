package com.T82.ticket.config.util;

import com.T82.ticket.dto.request.ChoiceSeatsRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
@RequiredArgsConstructor
public class LockSeatAspect {

    private final RedissonClient redissonClient;

    @Around("@annotation(LockSeat)")
    public Object lockSeat(ProceedingJoinPoint joinPoint) throws Throwable {
        Object[] args = joinPoint.getArgs();
        List<ChoiceSeatsRequest> requests = (List<ChoiceSeatsRequest>) args[0];  // 첫 번째 인자를 List<ChoiceSeatsRequest>로 가정

        for (ChoiceSeatsRequest request : requests) {
            Long seatId = request.seatId();  // 각 요청에서 seatId 추출
            String lockKey = "lock:seat:" + seatId;
            RLock lock = redissonClient.getLock(lockKey);

            boolean isLocked = false;
            try {
                isLocked = lock.tryLock(5, 360, TimeUnit.SECONDS);
                if (!isLocked) {
                    throw new RuntimeException("락 획득 실패 " + lockKey);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException("락 획득 중 인터럽트 발생: " + lockKey, e);
            } finally {
                if (isLocked) {
                    lock.unlock();  // 각 seatId에 대해 lock 해제
                }
            }
        }

        return joinPoint.proceed();  // 원래 메서드 실행
    }
}