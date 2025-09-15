package org.woven.digicommerce.tokensvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.tokensvc.LoginRequest;
import org.woven.digicommerce.tokensvc.annotation.LoginEntryExit;
import org.woven.digicommerce.tokensvc.annotation.MethodProcessingTime;
import org.woven.digicommerce.tokensvc.service.TokenService;

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
    public ResponseEntity<String> login(@RequestBody LoginRequest request) {
        String token = tokenService.generateToken(request.getUsername(), request.getSecret());
        return ResponseEntity.ok(token);
    }
    
    @PostMapping("/logout")
    @LoginEntryExit
    @MethodProcessingTime
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        tokenService.revokeToken(token);
        return ResponseEntity.ok("Logged out");
    }
    
    @PostMapping("/validate")
    @LoginEntryExit
    @MethodProcessingTime
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = tokenService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}