package com.T82.ticket.config.util;


import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ") && jwtUtil.validToken(bearerToken.substring(7))) {
            String token = bearerToken.substring(7);
            TokenInfo tokenInfo = jwtUtil.parseToken(token);

            //추출한 토큰을 정책에 주입(Context Holder)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(tokenInfo,null, tokenInfo.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);


        }
        //다른 필터가 있으면 다음으로 넘기는 함수
        filterChain.doFilter(request, response);
    }
}