package org.woven.digicommerce.ordersvc.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.woven.digicommerce.ordersvc.entity.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserId(Long userId, Pageable pageable);
    Page<Order> findByStatus(String status, Pageable pageable);
    Page<Order> findByUserIdAndStatus(Long userId, String status, Pageable pageable);
}