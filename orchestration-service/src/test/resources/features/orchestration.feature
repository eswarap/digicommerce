Feature: Orchestration Service
  As a client
  I want to get user data with their orders
  So that I can display complete user information

  Scenario: Get user orders by user ID
    Given the orchestration service is running
    When I get user orders for user ID 1
    Then I should receive user data with their orders

  Scenario: Get user orders by username
    Given the orchestration service is running
    When I get user orders for username "john"
    Then I should receive user data with their orders