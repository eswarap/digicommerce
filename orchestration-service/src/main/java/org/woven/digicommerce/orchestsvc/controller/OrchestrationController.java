package org.woven.digicommerce.orchestsvc.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.orchestsvc.dto.UserOrderSummary;
import org.woven.digicommerce.orchestsvc.service.OrchestrationService;

@RestController
public class OrchestrationController {
    private final OrchestrationService orchestrationService;
    
    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }
    
    @GetMapping("/user/{userId}/orders")
    public UserOrderSummary getUserOrderSummary(@PathVariable Long userId, @RequestHeader("Authorization") String authHeader) {
        return orchestrationService.getUserOrderSummary(userId, authHeader);
    }
    
    @GetMapping("/username/{username}/orders")
    public UserOrderSummary getUserOrderSummaryByUsername(@PathVariable String username, @RequestHeader("Authorization") String authHeader) {
        return orchestrationService.getUserOrderSummaryByUsername(username, authHeader);
    }
}