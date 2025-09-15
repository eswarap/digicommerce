package org.woven.digicommerce.orchestsvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.woven.digicommerce.orchestsvc.dto.Order;

import java.util.List;

@Service
public class OrderServiceClient {
    private final WebClient webClient;
    
    @Value("${services.order.url:http://localhost:8082}")
    private String orderServiceUrl;
    
    public OrderServiceClient(WebClient webClient) {
        this.webClient = webClient;
    }
    
    public List<Order> getOrdersByUserName(String userName, String authHeader) {
        return webClient.get()
                .uri(orderServiceUrl + "/orders/user/{userName}", userName)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Order>>() {})
                .block();
    }
    
    public List<Order> getOrdersByUserId(Long id, String authHeader) {
        return webClient.get()
                .uri(orderServiceUrl + "/orders/{id}", id)
                .header("Authorization", authHeader)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<Order>>() {})
                .block();
    }
}