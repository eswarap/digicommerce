package org.woven.digicommerce.orchestsvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.woven.digicommerce.orchestsvc.dto.User;

@Service
public class UserServiceClient {
    private final WebClient webClient;
    
    @Value("${services.user.url:http://localhost:8081}")
    private String userServiceUrl;
    
    public UserServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public User getUserById(Long id, String authHeader) {
        return webClient.get()
                .uri(userServiceUrl + "/users/userid/{id}", id)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }
    
    public User getUserByUsername(String username, String authHeader) {
        return webClient.get()
                .uri(userServiceUrl + "/users/username/{username}", username)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(User.class)
                .block();
    }
}