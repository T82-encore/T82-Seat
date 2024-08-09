package com.T82.ticket.controller;

import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.service.WaitingQueueService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/v1/events")
public class QueueViewController {
    private final WaitingQueueService waitingQueueService;


    @GetMapping("/{eventId}/queue/status")
    public String getQueueStatusPage(@PathVariable Long eventId, @AuthenticationPrincipal TokenInfo tokenInfo, Model model) {
        Long totalMembers = waitingQueueService.getTotalMembers(eventId);
        Long userPosition = waitingQueueService.getUserPosition(eventId, tokenInfo.id());
        model.addAttribute("eventId", eventId);
        model.addAttribute("token", tokenInfo);
        model.addAttribute("totalMembers", totalMembers);
        model.addAttribute("userPosition", userPosition);
        return "queue";
    }
}
