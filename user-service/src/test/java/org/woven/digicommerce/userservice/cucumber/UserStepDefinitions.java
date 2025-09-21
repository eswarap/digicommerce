package org.woven.digicommerce.userservice.cucumber;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.woven.digicommerce.userservice.entity.User;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserStepDefinitions {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;
    private ResponseEntity<User> userResponse;
    private ResponseEntity<List<User>> usersResponse;
    private Long userId;
    private String token;

    private String generateToken() {
        String randomUser = "user" + generateRandomString(6);
        String tokenUrl = "http://localhost:8090/token.svc/api/v1/auth/login";
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<String> request = new HttpEntity<>(randomUser, headers);

        ResponseEntity<String> tokenResponse = restTemplate.postForEntity(tokenUrl, request, String.class);
        return tokenResponse.getStatusCode() == HttpStatus.OK ? tokenResponse.getBody() : null;
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

    @Given("the user service is running")
    public void theUserServiceIsRunning() {
        // Service is running via @SpringBootTest
    }

    @When("I create a user with username {string}, email {string}, firstName {string}, and lastName {string}")
    public void iCreateAUser(String username, String email, String firstName, String lastName) {
        String url = "http://localhost:" + port + "/user.svc/api/v1/users";
        User user = new User(username, email, firstName, lastName);
        
        token = generateToken();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<User> request = new HttpEntity<>(user, headers);

        userResponse = restTemplate.postForEntity(url, request, User.class);
        if (userResponse.getStatusCode() == HttpStatus.OK && userResponse.getBody().getId() != null) {
            User body = userResponse.getBody();
            userId =  body.getId();
        }
    }

    @Then("the user should be created successfully")
    public void theUserShouldBeCreatedSuccessfully() {
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userId);
    }

    @And("I have created a user with username {string}, email {string}, firstName {string}, and lastName {string}")
    public void iHaveCreatedAUser(String username, String email, String firstName, String lastName) {
        iCreateAUser(username, email, firstName, lastName);
        theUserShouldBeCreatedSuccessfully();
    }

    @When("I get all users")
    public void iGetAllUsers() {
        String url = "http://localhost:" + port + "/user.svc/api/v1/users/all";
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<User> request = new HttpEntity<>(headers);
        usersResponse = restTemplate.exchange(url, HttpMethod.GET, request, new ParameterizedTypeReference<>(){});
    }

    @Then("I should receive a list of users")
    public void iShouldReceiveAListOfUsers() {
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userResponse.getBody());
        assertTrue(userResponse.getBody().getId() > 0);
    }

    @When("I get user by username {string}")
    public void iGetUserByUsername(String username) {
        String url = "http://localhost:" + port + "/user.svc/api/v1/users/username/" + username;
        HttpHeaders headers = new HttpHeaders();
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<User> request = new HttpEntity<>(headers);
        userResponse = restTemplate.exchange(url, HttpMethod.GET, request, User.class);
    }

    @Then("I should receive the user details")
    public void iShouldReceiveTheUserDetails() {
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
        assertNotNull(userResponse.getBody());
        assertTrue(userResponse.getBody().getId() > 0);
    }

    @When("I update the user email to {string}")
    public void iUpdateTheUserEmailTo(String email) {
        String url = "http://localhost:" + port + "/user.svc/api/v1/users/" + userId;
        User updateUser = new User(null, email, "John", "Doe");
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (token != null) {
            headers.set("Authorization", "Bearer " + token);
        }
        HttpEntity<User> request = new HttpEntity<>(updateUser, headers);
        
        userResponse = restTemplate.exchange(url, HttpMethod.PUT, request, User.class);
    }

    @Then("the user should be updated successfully")
    public void theUserShouldBeUpdatedSuccessfully() {
        assertEquals(HttpStatus.OK, userResponse.getStatusCode());
    }
}