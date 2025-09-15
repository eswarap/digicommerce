package org.woven.digicommerce.orchestsvc.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserOrderSummary {
    private User user;
    private List<Order> orders;
    private int totalOrders;
}