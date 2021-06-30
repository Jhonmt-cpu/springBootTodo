package com.todo.spring.shared.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todo.spring.modules.users.dtos.UserAuthenticatedDTO;
import com.todo.spring.modules.users.models.User;
import io.jsonwebtoken.*;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthenticationService {
    private static final String key = "e85267357e1786c1c396743bccd4dfe5";

    private static long expirationMilis = 86400000L;

    public static String createToken(User user) {
        long nowMillis = System.currentTimeMillis();
        Date now = new Date(nowMillis);
        Date expirationDate = new Date(nowMillis + expirationMilis);

        UserAuthenticatedDTO userAuthenticated = UserAuthenticatedDTO.toDTO(user);

        Claims claims = Jwts.claims().setSubject(null);
        claims.put("user", userAuthenticated);

        return Jwts.builder()
                .setIssuedAt(now)
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, key)
                .compact();
    }

    public static UserAuthenticatedDTO getUser(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(key)
                .parseClaimsJws(token)
                .getBody();

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.convertValue(claims.get("user"), UserAuthenticatedDTO.class);
    }

    public static boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
