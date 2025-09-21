package org.woven.digicommerce.tokensvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.common.LoginEntryExit;
import org.woven.digicommerce.common.MethodProcessingTime;
import org.woven.digicommerce.tokensvc.service.TokenService;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class TokenController {
    private final TokenService tokenService;
    
    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @PostMapping("/login")
    @LoginEntryExit
    @MethodProcessingTime
    public ResponseEntity<Map<String, String>> login(@RequestBody String username) {
        String token = tokenService.generateToken(username);
        String refreshToken = tokenService.generateRefreshToken(username);
        return ResponseEntity.ok(Map.of(
            "accessToken", token,
            "refreshToken", refreshToken,
            "tokenType", "Bearer"
        ));
    }
    
    @PostMapping("/refresh")
    @MethodProcessingTime
    public ResponseEntity<Map<String, String>> refresh(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.replace("Bearer ", "");
        String newToken = tokenService.refreshToken(refreshToken);
        return ResponseEntity.ok(Map.of(
            "accessToken", newToken,
            "tokenType", "Bearer"
        ));
    }
    
    @PostMapping("/logout")
    @LoginEntryExit
    @MethodProcessingTime
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        tokenService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/validate")
    @MethodProcessingTime
    public ResponseEntity<Map<String, Object>> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        Map<String, Object> validation = tokenService.validateTokenWithDetails(token);
        return ResponseEntity.ok(validation);
    }
}