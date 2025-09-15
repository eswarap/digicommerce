package org.woven.digicommerce.orchestsvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class TokenServiceClient {
    private final WebClient webClient;
    
    @Value("${services.token.url:http://localhost:8090}")
    private String tokenServiceUrl;
    
    public TokenServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public boolean validateToken(String token) {
        try {
            Boolean isValid = webClient.post()
                    .uri(tokenServiceUrl + "/auth/validate")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
            return Boolean.TRUE.equals(isValid);
        } catch (Exception e) {
            return false;
        }
    }
}