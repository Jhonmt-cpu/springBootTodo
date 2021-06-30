package com.todo.spring.shared.security;

import com.todo.spring.shared.exceptions.InvalidJwtAuthenticationException;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class AuthenticationService {
    private static final String key = "e85267357e1786c1c396743bccd4dfe5";

    private static long expirationMilis = 2L;

    public static String createToken(UUID userId) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expirationMilis);

        return Jwts.builder()
                .setIssuedAt(now)
                .setSubject(userId.toString())
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public static String getSubject(String token) {
        return Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public static boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            throw new InvalidJwtAuthenticationException();
        }
    }
}
