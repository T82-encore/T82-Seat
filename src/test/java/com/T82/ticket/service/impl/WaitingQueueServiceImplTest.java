package com.T82.ticket.service.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ZSetOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WaitingQueueServiceImplTest {

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @InjectMocks
    private WaitingQueueServiceImpl waitingQueueService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAddQueue() {
        Long eventId = 1L;
        String userId = "user123";
        String queueKey = "waiting:" + eventId;

        waitingQueueService.addQueue(eventId, userId);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> valueCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> scoreCaptor = ArgumentCaptor.forClass(Double.class);

        verify(zSetOperations, times(1)).add(keyCaptor.capture(), valueCaptor.capture(), scoreCaptor.capture());

        assertEquals(queueKey, keyCaptor.getValue());
        assertEquals(userId, valueCaptor.getValue());
        assertNotNull(scoreCaptor.getValue());
        // 적절한 범위 내에서 timestamp 검증
        assertTrue(System.currentTimeMillis() - scoreCaptor.getValue() < 1000);
    }

    @Test
    void testRemoveQueue() {
        Long eventId = 1L;
        String userId = "user123";
        String queueKey = "waiting:" + eventId;

        waitingQueueService.removeQueue(eventId, userId);

        verify(zSetOperations, times(1)).remove(queueKey, userId);
    }

    @Test
    void testGetTotalMembers() {
        Long eventId = 1L;
        String queueKey = "waiting:" + eventId;
        Long expectedMembers = 10L;

        when(zSetOperations.zCard(queueKey)).thenReturn(expectedMembers);

        Long totalMembers = waitingQueueService.getTotalMembers(eventId);

        assertEquals(expectedMembers, totalMembers);
    }

    @Test
    void testGetUserPosition() {
        Long eventId = 1L;
        String userId = "user123";
        String queueKey = "queue:" + eventId;
        long expectedPosition = 0L; // 사용자의 예상 위치

        when(zSetOperations.rank(queueKey, userId)).thenReturn(expectedPosition);

        Long userPosition = waitingQueueService.getUserPosition(eventId, userId);

        assertEquals(expectedPosition + 1, userPosition);
    }

}