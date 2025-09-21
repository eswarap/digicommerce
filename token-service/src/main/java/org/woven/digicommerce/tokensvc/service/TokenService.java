package org.woven.digicommerce.tokensvc.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import org.woven.digicommerce.tokensvc.entity.Token;
import org.woven.digicommerce.tokensvc.repository.TokenRepository;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class TokenService {
    private final TokenRepository tokenRepository;
    private final SecretKey key = Keys.hmacShaKeyFor("mySecretKeyForJWTTokenGeneration123456789".getBytes());
    private final SecretKey refreshKey = Keys.hmacShaKeyFor("myRefreshSecretKeyForJWTTokenGeneration987654321".getBytes());

    public TokenService(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    public String generateToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + 3600000); // 1 hour
        String jwt = Jwts.builder()
                .subject(username)
                .expiration(expiration)
                .signWith(key)
                .compact();
        
        Token token = new Token(jwt, username, LocalDateTime.now().plusHours(1));
        tokenRepository.save(token);
        return jwt;
    }
    
    public String generateRefreshToken(String username) {
        Date expiration = new Date(System.currentTimeMillis() + 604800000); // 7 days
        return Jwts.builder()
                .subject(username)
                .expiration(expiration)
                .signWith(refreshKey)
                .compact();
    }
    
    public String refreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parser().verifyWith(refreshKey).build().parseSignedClaims(refreshToken).getPayload();
            return generateToken(claims.getSubject());
        } catch (Exception e) {
            throw new RuntimeException("Invalid refresh token");
        }
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return tokenRepository.findFirstByTokenAndRevokedFalse(token).isPresent();
        } catch (Exception e) {
            return false;
        }
    }
    
    public Map<String, Object> validateTokenWithDetails(String token) {
        Map<String, Object> result = new HashMap<>();
        try {
            Claims claims = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
            boolean exists = tokenRepository.findFirstByTokenAndRevokedFalse(token).isPresent();
            result.put("valid", exists);
            result.put("username", claims.getSubject());
            result.put("expiration", claims.getExpiration());
        } catch (Exception e) {
            result.put("valid", false);
            result.put("error", e.getMessage());
        }
        return result;
    }
    
    public void revokeToken(String token) {
        tokenRepository.findFirstByTokenAndRevokedFalse(token)
                .ifPresent(t -> {
                    t.setRevoked(true);
                    tokenRepository.save(t);
                });
    }
}