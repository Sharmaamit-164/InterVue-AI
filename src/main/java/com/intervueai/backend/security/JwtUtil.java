package com.intervueai.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // Create signing key
    private SecretKey getSigningKey() {

        return Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    // Generate JWT token
    public String generateToken(
            String email,
            String role
    ) {

        Date now = new Date();

        Date expiryDate =
                new Date(now.getTime() + expiration);

        return Jwts.builder()
                .subject(email)
                .claim("role", role)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    // Extract email from token
    public String extractEmail(String token) {

        return getClaims(token)
                .getSubject();
    }

    // Extract role from token
    public String extractRole(String token) {

        return getClaims(token)
                .get("role", String.class);
    }

    // Validate JWT
    public boolean isTokenValid(String token) {

        try {

            Claims claims = getClaims(token);

            // Check that subject/email exists
            String email = claims.getSubject();

            // Check that token is not expired
            Date expirationDate = claims.getExpiration();

            return email != null
                    && expirationDate != null
                    && expirationDate.after(new Date());

        } catch (Exception e) {

            return false;
        }
    }

    // Parse and verify JWT
    private Claims getClaims(String token) {

        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}