package org.woven.digicommerce.orchestsvc.controller;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.woven.digicommerce.orchestsvc.dto.UserOrderSummary;
import org.woven.digicommerce.orchestsvc.service.OrchestrationService;

@RestController
@RequestMapping("/orchestration")
public class OrchestrationController {
    private final OrchestrationService orchestrationService;
    
    public OrchestrationController(OrchestrationService orchestrationService) {
        this.orchestrationService = orchestrationService;
    }
    
    @GetMapping("/users/{identifier}/orders")
    @Cacheable(value = "userOrders", key = "#identifier + '_' + #isUsername")
    public UserOrderSummary getUserOrders(
            @PathVariable String identifier,
            @RequestParam(defaultValue = "false") boolean isUsername,
            @RequestHeader("Authorization") String authHeader) {
        return isUsername ? 
            orchestrationService.getUserOrderSummaryByUsername(identifier, authHeader) :
            orchestrationService.getUserOrderSummary(Long.parseLong(identifier), authHeader);
    }
}