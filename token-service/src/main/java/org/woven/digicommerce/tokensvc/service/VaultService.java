package org.woven.digicommerce.tokensvc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

@Service
public class VaultService {
    
    @Value("${jwt.secret:mySecretKeyForJWTTokenGeneration123456789012}")
    private String fallbackSecret;
    
    @Value("${aws.region:us-east-1}")
    private String awsRegion;
    
    @Value("${aws.enabled:true}")
    private boolean awsEnabled;
    
    public String getSecret(String secretName) {
        if ("jwt-secret".equals(secretName)) {
            if (!awsEnabled) {
                return fallbackSecret;
            }
            
            try {
                SecretsManagerClient client = SecretsManagerClient.builder()
                        .region(Region.of(awsRegion))
                        .build();
                
                String secret = client.getSecretValue(GetSecretValueRequest.builder()
                        .secretId(secretName)
                        .build()).secretString();
                        
                client.close();
                return secret;
            } catch (Exception e) {
                return fallbackSecret;
            }
        }
        return null;
    }
}