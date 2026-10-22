package com.triage.ticket_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
public class JWTService {
    private static final Set<String> ALLOWED_ROLES = Set.of("CUSTOMER", "AGENT");
    private final Key signingKey;
    public JWTService(@Value("${jwt.secret}") String secret) {
        // Fails at startup (WeakKeyException) if the secret is shorter than 32 bytes
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }
    public Optional<TokenPrincipal> parse(String token){
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(signingKey)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            String username = claims.getSubject();
            String role = claims.get("role", String.class);

            if (username == null || role == null || !ALLOWED_ROLES.contains(role)) {
                return Optional.empty();
            }
            return Optional.of(new TokenPrincipal(username, role));

        }catch (JwtException | IllegalArgumentException ex) {
            log.debug("Rejected JWT: {}", ex.getClass().getSimpleName());
            return Optional.empty();
        }
    }
    public record TokenPrincipal(String username, String role) {
    }
}
