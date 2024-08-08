package com.T82.ticket.controller;


import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;
    private final RedisTemplate<String, String> redisTemplate;
    /*
    * 해당 이벤트의 대기열 정보 가져오기
    * */
    @GetMapping("/{eventId}/queue/total/users")
    public Long getTotalMembers(@PathVariable Long eventId) {
        return waitingQueueService.getTotalMembers(eventId);
    }

    /*
     * 해당 이벤트의 회원이 몇 번 째 자리인지
     * */
    @GetMapping("/{eventId}/queue/position")
    public Long getUserPosition(@PathVariable Long eventId , @AuthenticationPrincipal TokenInfo tokenInfo) {
        return waitingQueueService.getUserPosition(eventId, tokenInfo.id());
    }

    @PostMapping("/{eventId}/queue/entry")
    public void queueEntry(@PathVariable Long eventId , @AuthenticationPrincipal TokenInfo tokenInfo){
        waitingQueueService.addQueue(eventId, tokenInfo.id());
    }
    /*
     * Polling 방식으로 대기열의 상황을 알려주기 위한 컨트롤러
     *
     *
     */
    @GetMapping("/{eventId}/queue/status/user")
    public Map<String, String> getQueueStatus(@PathVariable Long eventId, @AuthenticationPrincipal TokenInfo tokenInfo) {
        String status = redisTemplate.opsForValue().get("queue:status:" + eventId + ":" + tokenInfo.id());
        Map<String, String> response = new HashMap<>();
        response.put("status", status != null ? status : "IN_PROGRESS");
        return response;
    }
}
