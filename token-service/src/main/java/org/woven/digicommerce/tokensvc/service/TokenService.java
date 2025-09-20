package org.woven.digicommerce.tokensvc.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.woven.digicommerce.tokensvc.entity.Token;
import org.woven.digicommerce.tokensvc.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final SecretKey key = Keys.hmacShaKeyFor("mySecretKeyForJWTTokenGeneration123456789".getBytes());

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(String username, String secret) {
        if (secret == null || secret.getBytes().length < 32) {
            throw new IllegalArgumentException("Secret key must be at least 32 bytes (256 bits) for HS256.");
        }
        SecretKey customKey = Keys.hmacShaKeyFor(secret.getBytes()); // Use user-provided secret
        Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hour
        String jwt = Jwts.builder()
                .subject(username)
                .expiration(expiration)
                .signWith(customKey)
                .compact();
        
        Token token = new Token(jwt, username, LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);
        return jwt;
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return tokenRepository.findFirstByTokenAndRevokedFalse(token).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    public void revokeToken(String token) {
        tokenRepository.findFirstByTokenAndRevokedFalse(token)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    tokenRepository.save(t);
                });
    }
}