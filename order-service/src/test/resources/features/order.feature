Feature: Order Management
  As a user
  I want to manage orders
  So that I can track customer purchases

  Scenario: Create a new order
    Given the order service is running
    When I create an order with userId 1, product "Laptop", quantity 1, and price 999.99
    Then the order should be created successfully

  Scenario: Get all orders
    Given the order service is running
    And I have created an order with userId 1, product "Laptop", quantity 1, and price 999.99
    When I get all orders
    Then I should receive a list of orders

  Scenario: Get orders by user
    Given the order service is running
    And I have created an order with userId 1, product "Laptop", quantity 1, and price 999.99
    When I get orders for userId 1
    Then I should receive orders for that user

  Scenario: Update order status
    Given the order service is running
    And I have created an order with userId 1, product "Laptop", quantity 1, and price 999.99
    When I update the order status to "SHIPPED"
    Then the order status should be updated successfully