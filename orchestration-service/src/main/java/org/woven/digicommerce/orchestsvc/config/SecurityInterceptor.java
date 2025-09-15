package org.woven.digicommerce.orchestsvc.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.woven.digicommerce.orchestsvc.service.TokenServiceClient;

@Component
public class SecurityInterceptor implements HandlerInterceptor {
    private final TokenServiceClient tokenServiceClient;
    
    public SecurityInterceptor(TokenServiceClient tokenServiceClient) {
        this.tokenServiceClient = tokenServiceClient;
    }
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        String token = authHeader.substring(7);
        if (!tokenServiceClient.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }
        
        return true;
    }
}