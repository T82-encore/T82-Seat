package com.T82.ticket.controller;


import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class WaitingQueueController {

    private final WaitingQueueService waitingQueueService;
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

    @PostMapping("/{eventId}/queue/remove")
    public void queueRemove(@PathVariable Long eventId , @AuthenticationPrincipal TokenInfo tokenInfo){
        waitingQueueService.removeQueue(eventId, tokenInfo.id());
    }

}
