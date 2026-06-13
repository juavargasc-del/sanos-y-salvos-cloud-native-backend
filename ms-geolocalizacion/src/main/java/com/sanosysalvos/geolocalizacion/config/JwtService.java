package com.sanosysalvos.geolocalizacion.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JwtService {

    private static final String SECRET_KEY =
            "miclavesupersecretamiclavesupersecret12345";

    private Key getSigningKey() {

        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public String extraerEmail(String token) {

        return extraerClaims(token).getSubject();
    }

    private Claims extraerClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean tokenValido(String token, String email) {

        final String emailToken = extraerEmail(token);

        return emailToken.equals(email);
    }
}
