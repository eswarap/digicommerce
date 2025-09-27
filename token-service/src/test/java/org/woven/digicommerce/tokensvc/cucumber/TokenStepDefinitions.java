package org.woven.digicommerce.tokensvc.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.security.SecureRandom;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TokenStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String token;
    private ResponseEntity<Map> response;

    @Given("the token service is running")
    public void theTokenServiceIsRunning() {
        // Service is running via @SpringBootTest
    }

    
    @When("I login with username {string} and secret {string}")
    public void iLoginWithUsernameAndSecret(String username, String secret) {
        String randomUser = "user" + generateRandomString();
        String url = "http://localhost:" + port + "/token.svc/api/v1/auth/login";
        String authJson = "{\"username\":\"" + randomUser + "\"}";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(authJson, headers);
        response = restTemplate.postForEntity(url, request, Map.class);

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            Map<String, String> responseBody = response.getBody();
            if (responseBody != null) {
                @SuppressWarnings("unchecked")
                Map<String, String> body = responseBody;
                token = body.get("accessToken");
            }
        }
    }
    
    private String generateRandomString() {
        String chars = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    @Then("I should receive a valid JWT token")
    public void iShouldReceiveAValidJWTToken() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(token);
        assertFalse(token.isEmpty());
    }

    @When("I validate the token")
    public void iValidateTheToken() {
        String url = "http://localhost:" + port + "/token.svc/api/v1/auth/validate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<Void> request = new HttpEntity<>(headers);
        
        response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET, request, Map.class);
    }

    @Then("the token should be valid")
    public void theTokenShouldBeValid() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull( response.getBody());
    }

    @When("I logout with the token")
    public void iLogoutWithTheToken() {
        String url = "http://localhost:" + port + "/token.svc/api/v1/auth/logout";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        response = restTemplate.postForEntity(url, request, Map.class);
    }

    @Then("the token should be revoked")
    public void theTokenShouldBeRevoked() {
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @And("validation should fail for the revoked token")
    public void validationShouldFailForTheRevokedToken() {
        iValidateTheToken();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        @SuppressWarnings("unchecked")
        Map<String, Object> body = (Map<String, Object>) response.getBody();
        assertEquals(false, body.get("valid"));
    }
}