package org.woven.digicommerce.orchestsvc.service;

import org.springframework.stereotype.Service;
import org.woven.digicommerce.orchestsvc.dto.Order;
import org.woven.digicommerce.orchestsvc.dto.User;
import org.woven.digicommerce.orchestsvc.dto.UserOrderSummary;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrchestrationService {
    private final UserServiceClient userServiceClient;
    private final OrderServiceClient orderServiceClient;
    
    public OrchestrationService(UserServiceClient userServiceClient, OrderServiceClient orderServiceClient) {
        this.userServiceClient = userServiceClient;
        this.orderServiceClient = orderServiceClient;
    }
    
    public UserOrderSummary getUserOrderSummary(Long userId, String authHeader) {
        var userResponse = userServiceClient.getUserById(userId, authHeader);
        if (userResponse == null) {
            throw new RuntimeException("User not found");
        }
        
        var orderResponses = orderServiceClient.getOrdersByUserId(userResponse.getUserId(), authHeader);
        
        User user = new User();
        user.setUserId(userResponse.getUserId());
        
        List<Order> orders = orderResponses.stream().map(orderResponse -> {
            Order order = new Order();
            order.setOrderId(orderResponse.getOrderId());
            order.setUserId(userResponse.getUserId());
            return order;
        }).collect(Collectors.toList());
        
        UserOrderSummary summary = new UserOrderSummary();
        summary.setUser(user);
        summary.setOrders(orders);
        summary.setTotalOrders(orders.size());
        
        return summary;
    }
    
    public UserOrderSummary getUserOrderSummaryByUsername(String username, String authHeader) {
        var userResponse = userServiceClient.getUserByUsername(username, authHeader);
        if (userResponse == null) {
            throw new RuntimeException("User not found");
        }
        
        var orderResponses = orderServiceClient.getOrdersByUserName(username, authHeader);
        
        User user = new User();
        user.setUserId(userResponse.getUserId());
        
        List<Order> orders = orderResponses.stream().map(orderResponse -> {
            Order order = new Order();
            order.setOrderId(orderResponse.getOrderId());
            order.setUserId(userResponse.getUserId());
            return order;
        }).collect(Collectors.toList());
        
        UserOrderSummary summary = new UserOrderSummary();
        summary.setUser(user);
        summary.setOrders(orders);
        summary.setTotalOrders(orders.size());
        
        return summary;
    }
}