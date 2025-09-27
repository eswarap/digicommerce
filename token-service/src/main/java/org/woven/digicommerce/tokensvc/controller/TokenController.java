package org.woven.digicommerce.tokensvc.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.common.LoginEntryExit;
import org.woven.digicommerce.common.MethodProcessingTime;
import org.woven.digicommerce.tokensvc.dto.AuthRequest;
import org.woven.digicommerce.tokensvc.dto.AuthResponse;
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
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        String token = tokenService.generateToken(request.getUsername(), request.getSecret());
        AuthResponse response = new AuthResponse(token, "Bearer");
        return ResponseEntity.ok(response);
    }
    

    
    @PostMapping("/logout")
    @LoginEntryExit
    @MethodProcessingTime
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        tokenService.revokeToken(token);
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/validate")
    @MethodProcessingTime
    public ResponseEntity<Boolean> validate(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = tokenService.validateToken(token);
        return ResponseEntity.ok(isValid);
    }
}