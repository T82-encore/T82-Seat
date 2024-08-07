package com.T82.ticket.service;

public interface WaitingQueueService {

    void addQueue(Long eventId ,String userId);

    void removeQueue(Long eventId ,String userId);

    Long getTotalMembers(Long eventId);

    Long getUserPosition(Long eventId ,String userId);

}
