package org.woven.digicommerce.notification.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NotificationHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    
    private final DynamoDbClient dynamoDb = DynamoDbClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            String path = request.getPath();
            String method = request.getHttpMethod();
            
            return switch (path) {
                case "/api/notifications/send" -> handleSendNotification(request);
                case "/api/notifications" -> handleGetNotifications(request);
                default -> createResponse(404, "Not Found");
            };
        } catch (Exception e) {
            return createResponse(500, "Internal Server Error: " + e.getMessage());
        }
    }
    
    private APIGatewayProxyResponseEvent handleSendNotification(APIGatewayProxyRequestEvent request) {
        try {
            JsonNode body = objectMapper.readTree(request.getBody());
            String message = body.get("message").asText();
            String recipient = body.get("recipient").asText();
            
            String notificationId = UUID.randomUUID().toString();
            
            Map<String, AttributeValue> item = Map.of(
                    "notificationId", AttributeValue.builder().s(notificationId).build(),
                    "message", AttributeValue.builder().s(message).build(),
                    "recipient", AttributeValue.builder().s(recipient).build(),
                    "timestamp", AttributeValue.builder().s(Instant.now().toString()).build(),
                    "status", AttributeValue.builder().s("sent").build()
            );
            
            dynamoDb.putItem(PutItemRequest.builder()
                    .tableName("notifications")
                    .item(item)
                    .build());
            
            return createResponse(200, "{\"notificationId\":\"" + notificationId + "\",\"status\":\"sent\"}");
        } catch (Exception e) {
            return createResponse(400, "Invalid request: " + e.getMessage());
        }
    }
    
    private APIGatewayProxyResponseEvent handleGetNotifications(APIGatewayProxyRequestEvent request) {
        try {
            ScanResponse response = dynamoDb.scan(ScanRequest.builder()
                    .tableName("notifications")
                    .build());
            
            StringBuilder notifications = new StringBuilder("[");
            boolean first = true;
            
            for (Map<String, AttributeValue> item : response.items()) {
                if (!first) notifications.append(",");
                notifications.append("{")
                        .append("\"notificationId\":\"").append(item.get("notificationId").s()).append("\",")
                        .append("\"message\":\"").append(item.get("message").s()).append("\",")
                        .append("\"recipient\":\"").append(item.get("recipient").s()).append("\",")
                        .append("\"timestamp\":\"").append(item.get("timestamp").s()).append("\",")
                        .append("\"status\":\"").append(item.get("status").s()).append("\"")
                        .append("}");
                first = false;
            }
            notifications.append("]");
            
            return createResponse(200, notifications.toString());
        } catch (Exception e) {
            return createResponse(500, "Error retrieving notifications: " + e.getMessage());
        }
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