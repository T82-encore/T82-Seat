package com.T82.ticket.config.util;

import io.jsonwebtoken.Claims;
import org.springframework.cglib.core.Local;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
public record TokenInfo (
        String id, String email) implements UserDetails {

    public static TokenInfo fromClaims(Claims claims) {
        String id = claims.get("id", String.class);
        String email = claims.get("email", String.class);
//        LocalDate birthDate = LocalDate.parse(claims.get("birthDate", String.class));
//        String name = claims.get("name", String.class);
//        String phoneNumber = claims.get("phoneNumber", String.class);
        return new TokenInfo(id, email);


    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getUsername() {
        return "";
    }
}
