package org.woven.digicommerce.ordersvc.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class OrderStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<String> response;
    private Long orderId;
    private String token;

    private String generateToken() {
        String randomUser = "user" + generateRandomString(6);
        String tokenUrl = "http://localhost:8090/token.svc/api/v1/auth/login";
        String authJson = "{\"username\":\"" + randomUser + "\"}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(authJson, headers);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, request, String.class);
        if (tokenResponse.getStatusCode() == HttpStatus.OK && tokenResponse.getBody().contains("accessToken")) {
            String body = tokenResponse.getBody();
            return body.substring(body.indexOf("\"accessToken\":\"") + 14, body.indexOf("\"", body.indexOf("\"accessToken\":\"") + 14));
        }
        return null;
    }
    
    private String generateRandomString(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Given("the order service is running")
    public void theOrderServiceIsRunning() {
        // Service is running via @SpringBootTest
    }

    @When("I create an order with userId {long}, product {string}, quantity {int}, and price {double}")
    public void iCreateAnOrder(Long userId, String product, int quantity, double price) {
        String url = "http://localhost:" + port + "/order.svc/api/v1/orders";
        String orderJson = String.format("{\"userId\":%d,\"productName\":\"%s\",\"quantity\":%d,\"price\":%.2f}",
                                       userId, product, quantity, price);
        
        token = generateToken();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(orderJson, headers);
        
        response = restTemplate.postForEntity(url, request, String.class);
        if (response.getStatusCode() == HttpStatus.OK && response.getBody().contains("\"orderId\":")) {
            String body = response.getBody();
            orderId = Long.parseLong(body.substring(body.indexOf("\"orderId\":") + 10, body.indexOf(",", body.indexOf("\"orderId\":"))));
        }
    }

    @Then("the order should be created successfully")
    public void theOrderShouldBeCreatedSuccessfully() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(orderId);
    }

    @And("I have created an order with userId {long}, product {string}, quantity {int}, and price {double}")
    public void iHaveCreatedAnOrder(Long userId, String product, int quantity, double price) {
        iCreateAnOrder(userId, product, quantity, price);
        theOrderShouldBeCreatedSuccessfully();
    }

    @When("I get all orders")
    public void iGetAllOrders() {
        String url = "http://localhost:" + port + "/order.svc/api/v1/orders";
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    @Then("I should receive a list of orders")
    public void iShouldReceiveAListOfOrders() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("["));
    }

    @When("I get orders for userId {long}")
    public void iGetOrdersForUser(Long userId) {
        String url = "http://localhost:" + port + "/order.svc/api/v1/orders/user/" + userId;
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(headers);
        response = restTemplate.exchange(url, HttpMethod.GET, request, String.class);
    }

    @Then("I should receive orders for that user")
    public void iShouldReceiveOrdersForThatUser() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @When("I update the order status to {string}")
    public void iUpdateTheOrderStatusTo(String status) {
        String url = "http://localhost:" + port + "/order.svc/api/v1/orders/" + orderId + "/status";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<String> request = new HttpEntity<>(status, headers);
        
        response = restTemplate.exchange(url, HttpMethod.PUT, request, String.class);
    }

    @Then("the order status should be updated successfully")
    public void theOrderStatusShouldBeUpdatedSuccessfully() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}