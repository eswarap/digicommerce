package org.woven.digicommerce.tokensvc.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SecretKey key = Keys.hmacShaKeyFor(System.getenv("JWT_SECRET").getBytes());
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String path = request.getPath();
            String method = request.getHttpMethod();
            
            return switch (path) {
                case "/api/auth/login" -> handleLogin(request);
                case "/api/auth/validate" -> handleValidate(request);
                case "/api/auth/logout" -> handleLogout(request);
                default -> createResponse(404, "Not Found");
            };
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }
    
    private APIGatewayProxyResponseEvent handleLogin(APIGatewayProxyRequestEvent request) {
        String username = request.getBody();
        String token = generateToken(username);
        storeToken(token, username);
        return createResponse(200, token);
    }
    
    private APIGatewayProxyResponseEvent handleValidate(APIGatewayProxyRequestEvent request) {
        String token = extractToken(request);
        boolean isValid = validateToken(token);
        return createResponse(200, String.valueOf(isValid));
    }
    
    private APIGatewayProxyResponseEvent handleLogout(APIGatewayProxyRequestEvent request) {
        String token = extractToken(request);
        revokeToken(token);
        return createResponse(200, "Logged out");
    }
    
    private String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 86400000))
                .signWith(key)
                .compact();
    }
    
    private boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
            return !isTokenRevoked(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    private void storeToken(String token, String username) {
        Map<String, AttributeValue> item = Map.of(
                "tokenId", AttributeValue.builder().s(token).build(),
                "username", AttributeValue.builder().s(username).build(),
                "revoked", AttributeValue.builder().bool(false).build()
        );
        
        dynamoDb.putItem(PutItemRequest.builder()
                .tableName("tokens")
                .item(item)
                .build());
    }
    
    private boolean isTokenRevoked(String token) {
        try {
            GetItemResponse response = dynamoDb.getItem(GetItemRequest.builder()
                    .tableName("tokens")
                    .key(Map.of("tokenId", AttributeValue.builder().s(token).build()))
                    .build());
            
            return response.item().isEmpty() || 
                   response.item().get("revoked").bool();
        } catch (Exception e) {
            return true;
        }
    }
    
    private void revokeToken(String token) {
        dynamoDb.updateItem(UpdateItemRequest.builder()
                .tableName("tokens")
                .key(Map.of("tokenId", AttributeValue.builder().s(token).build()))
                .updateExpression("SET revoked = :revoked")
                .expressionAttributeValues(Map.of(":revoked", AttributeValue.builder().bool(true).build()))
                .build());
    }
    
    private String extractToken(APIGatewayProxyRequestEvent request) {
        String authHeader = request.getHeaders().get("Authorization");
        return authHeader != null ? authHeader.replace("Bearer ", "") : "";
    }
    
    private APIGatewayProxyResponseEvent createResponse(int statusCode, String body) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Access-Control-Allow-Origin", "*");
        
        return new APIGatewayProxyResponseEvent()
                .withStatusCode(statusCode)
                .withHeaders(headers)
                .withBody(body);
    }
}