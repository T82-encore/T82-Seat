package com.T82.ticket.service.impl;

import com.T82.ticket.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Set;

import java.util.concurrent.TimeUnit;


@Service
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueServiceImpl implements WaitingQueueService {

    private final ZSetOperations<String, String> zSetOperations;
    private final RedisTemplate<String, String> redisTemplate;
    private static final String QUEUE_KEY_PREFIX = "waiting:";

    public String makeQueueKey(Long eventId) {
        return QUEUE_KEY_PREFIX + eventId;
    }


    @Override
    public void addQueue(Long eventId, String userId) {
        String queueKey = makeQueueKey(eventId);
        Boolean result = zSetOperations.add(queueKey, userId, System.currentTimeMillis());
        log.info("대기열 추가 회원 ID: {} queueKey: {} 결과: {}", userId, queueKey, result);
    }

    @Override
    public void removeQueue(Long eventId, String userId) {
        String queueKey = makeQueueKey(eventId);
        Long result = zSetOperations.remove(queueKey, userId);
        log.info("대기열 삭제 회원 ID: {} queueKey: {} 결과: {}", userId, queueKey, result);

        if (result != null && result > 0) {
            redisTemplate.opsForValue().set("queue:status:" + eventId + ":" + userId, "ENDED",1, TimeUnit.MINUTES);
        }
    }

    @Override
    public Long getTotalMembers(Long eventId) {
        String queueKey = makeQueueKey(eventId);
        Long totalMembers = zSetOperations.zCard(queueKey);
        log.info("대기열 내 총 인원 {}: {}", queueKey, totalMembers);
        return totalMembers;
    }

    @Override
    public Long getUserPosition(Long eventId, String userId) {
        String queueKey = makeQueueKey(eventId);
        Long position = zSetOperations.rank(queueKey, userId);
        log.info("회원 ID {} 현재 내 위치 {}: {}", userId, queueKey, position + 1);
        return position + 1 ;
    }

    @Scheduled(fixedRate = 60000)

    public void processQueue() {
        Set<String> keys = redisTemplate.keys(QUEUE_KEY_PREFIX + "*");
        if (keys != null) {
            for (String queueKey : keys) {
                Set<String> queue = zSetOperations.range(queueKey, 0, 0); // 첫 번째 사용자 가져오기
                if (queue != null && !queue.isEmpty()) {
                    String userId = queue.iterator().next();
                    try {
                        Long eventId = Long.parseLong(queueKey.split(":")[1]);
                        removeQueue(eventId, userId); // 이벤트 ID와 userId를 가져와 제거
                    } catch (NumberFormatException e) {
                        log.error("키 삭제: {}", queueKey);
                        redisTemplate.delete(queueKey);
                    }
                }
            }
        }
    }
}
