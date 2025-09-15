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
import org.woven.digicommerce.tokensvc.LoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TokenStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private String token;
    private ResponseEntity<String> response;

    @Given("the token service is running")
    public void theTokenServiceIsRunning() {
        // Service is running via @SpringBootTest
    }
    
    @When("I login with username {string} and secret {string}")
    public void iLoginWithUsernameAndSecret(String username, String secret) {
        String url = "http://localhost:" + port + "/token.svc/api/v1/auth/login";
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsername(username);
        loginRequest.setSecret(secret);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);
        response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            token = response.getBody().toString();
        }
    }

    @Then("I should receive a valid JWT token")
    public void iShouldReceiveAValidJWTToken() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(token);
        assertTrue(token.length() > 0);
    }

    @When("I validate the token")
    public void iValidateTheToken() {
        String url = "http://localhost:" + port + "/token.svc/api/v1/auth/validate";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);
        HttpEntity<String> request = new HttpEntity<>(headers);
        
        response = restTemplate.postForEntity(url, request, String.class);
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
        
        response = restTemplate.postForEntity(url, request, String.class);
    }

    @Then("the token should be revoked")
    public void theTokenShouldBeRevoked() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @And("validation should fail for the revoked token")
    public void validationShouldFailForTheRevokedToken() {
        iValidateTheToken();
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("false", response.getBody());
    }
}