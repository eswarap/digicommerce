Feature: User Management
  As a system administrator
  I want to manage users
  So that I can maintain user accounts

  Scenario: Create a new user
    Given the user service is running
    When I create a user with username "johndoe", email "john.doe@example.com", firstName "John", and lastName "Doe"
    Then the user should be created successfully

  Scenario: Get all users
    Given the user service is running
    And I have created a user with username "olivergarcia", email "oliver.garciae@example.com", firstName "Oliver", and lastName "Garcia"
    When I get all users
    Then I should receive a list of users

  Scenario: Get user by username
    Given the user service is running
    And I have created a user with username "liampeter", email "liam.peter@example.com", firstName "Liam", and lastName "Peter"
    When I get user by username "liampeter"
    Then I should receive the user details

  Scenario: Update user information
    Given the user service is running
    And I have created a user with username "adrian", email "adrian.north@example.com", firstName "Adrian", and lastName "North"
    When I update the user email to "adrian.north+update@example.com"
    Then the user should be updated successfully