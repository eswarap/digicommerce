Feature: Token Management
  As a user
  I want to manage authentication tokens
  So that I can access protected resources

  Scenario: Generate token for valid user
    Given the token service is running
    When I login with username "testuser" and secret "mySecretKeyForJWTTokenGeneration123456789"
    Then I should receive a valid JWT token

  Scenario: Generate token with username and secret
    Given the token service is running
    When I login with username "testuser" and secret "mySecretKeyForJWTTokenGeneration123456789"
    Then I should receive a valid JWT token

  Scenario: Validate a valid token
    Given the token service is running
    When I login with username "testuser" and secret "mySecretKeyForJWTTokenGeneration123456789"
    When I validate the token
    Then the token should be valid

  Scenario: Logout and revoke token
    Given the token service is running
    When I login with username "testuser" and secret "mySecretKeyForJWTTokenGeneration123456789"
    When I logout with the token
    Then the token should be revoked
    And validation should fail for the revoked token