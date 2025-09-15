package org.woven.digicommerce.orchestsvc.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrchestrationStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;

    @Given("the orchestration service is running")
    public void theOrchestrationServiceIsRunning() {
        // Service is running via @SpringBootTest
    }

    @When("I get user orders for user ID {int}")
    public void iGetUserOrdersForUserId(int userId) {
        String url = "http://localhost:" + port + "/api/orchestration/user/" + userId + "/orders";
        response = restTemplate.getForEntity(url, String.class);
    }

    @When("I get user orders for username {string}")
    public void iGetUserOrdersForUsername(String username) {
        String url = "http://localhost:" + port + "/api/orchestration/username/" + username + "/orders";
        response = restTemplate.getForEntity(url, String.class);
    }

    @Then("I should receive user data with their orders")
    public void iShouldReceiveUserDataWithTheirOrders() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"user\":") || response.getBody().contains("\"orders\":"));
    }
}