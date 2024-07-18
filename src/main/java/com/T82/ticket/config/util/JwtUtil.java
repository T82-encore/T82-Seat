package com.T82.ticket.config.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;


@Component
public class JwtUtil {
    private final Long expiration;
    private final SecretKey secret;

    //환경변수 설정한 값들 변수에 주입작업
    public JwtUtil(
            @Value("${jwt.expiration}") Long expiration,
            @Value("${jwt.secret}") String secret) {
        this.expiration = expiration;
        this.secret = Keys.hmacShaKeyFor(secret.getBytes());;
    }

    //JWT 토큰 정보 보기 작업
    public TokenInfo parseToken(String token) {
        try{Claims payload = (Claims) Jwts.parser()
                .verifyWith(secret)
                .build()
                .parse(token)
                .getPayload();
            return TokenInfo.fromClaims(payload);}
        catch(JwtException e){
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.",e);
        }
    }

    //JWT 토큰 만료됐는지 검증하는 작업
    public boolean validToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parse(token);
        }catch (Exception e){
            return false;
        }
        return true;
    }
}