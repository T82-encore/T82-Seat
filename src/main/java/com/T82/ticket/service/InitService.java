package com.T82.ticket.service;

import com.T82.ticket.config.util.TokenInfo;
import com.T82.ticket.dto.request.EventInitRequestDto;

public interface InitService {
    void initEventPlace(EventInitRequestDto req, TokenInfo tokenInfo);
}
