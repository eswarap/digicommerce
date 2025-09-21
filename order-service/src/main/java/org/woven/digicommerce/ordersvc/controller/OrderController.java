package org.woven.digicommerce.ordersvc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.ordersvc.entity.Order;
import org.woven.digicommerce.ordersvc.service.OrderService;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @GetMapping
    public Page<Order> getOrders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String status,
            Pageable pageable) {
        if (userId != null && status != null) {
            return orderService.getOrdersByUserAndStatus(userId, status, pageable);
        } else if (userId != null) {
            return orderService.getOrdersByUser(userId, pageable);
        } else if (status != null) {
            return orderService.getOrdersByStatus(status, pageable);
        }
        return orderService.getAllOrders(pageable);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderService.getOrderById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public Order createOrder(@RequestBody Order order) {
        return orderService.createOrder(order);
    }
    
    @PostMapping("/batch")
    public List<Order> createOrders(@RequestBody List<Order> orders) {
        return orderService.createOrders(orders);
    }
    
    @PutMapping("/{id}/status")
    public Order updateOrderStatus(@PathVariable Long id, @RequestBody String status) {
        return orderService.updateOrderStatus(id, status);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}