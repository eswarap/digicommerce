package org.woven.digicommerce.ordersvc.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.woven.digicommerce.ordersvc.entity.Order;
import org.woven.digicommerce.ordersvc.repository.OrderRepository;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrderService {
    private final OrderRepository orderRepository;
    
    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    public Page<Order> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Page<Order> getOrdersByUser(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }
    
    public Page<Order> getOrdersByStatus(String status, Pageable pageable) {
        return orderRepository.findByStatus(status, pageable);
    }
    
    public Page<Order> getOrdersByUserAndStatus(Long userId, String status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }
    
    public Order createOrder(Order order) {
        return orderRepository.save(order);
    }
    
    public List<Order> createOrders(List<Order> orders) {
        return orderRepository.saveAll(orders);
    }
    
    public Order updateOrderStatus(Long id, String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return orderRepository.save(order);
                })
                .orElseThrow(() -> new RuntimeException("Order not found"));
    }
    
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}