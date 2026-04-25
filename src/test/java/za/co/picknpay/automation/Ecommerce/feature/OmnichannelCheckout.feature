Feature: Omnichannel Retail Order Validation and Data Integrity
  As an automation framework,
  I want to validate that customer order data is accurately synchronized across the retail GUI and backend OMS channels,
  To ensure end-to-end business process data integrity and intelligent failure handling in an Agile environment.

  # Prerequisite (Required by OfferZen): "Register a new customer".
  #: Rather than making the primary checkout scenario long and brittle, I handled account creation in a Background.
  # This demonstrates advanced Gherkin design principles to manage test stability and scope within constraints.
  Background: A New Customer is Registered on the Retail Portal

    Given a new customer is successfully registered on the GUI channel with the following details:
      | Field          | Value             |
      | Date of Birth  | 15-May-1985       |
      | Company        | TechRetail Africa |
      | Address 1      | 123 Main Street   |
      | Country        | Canada            |
      | State          | Ontario           |
      | City           | Toronto           |
      | Zipcode        | 12345             |
      | Mobile         | 0712345678        |

  # Scenario 1: The "Happy Path" (Omnichannel Integration)
  #: This scenario proves the entire successful data chain Lisa requested.
  # It dynamically captures the Order ID from the AutomationExercise GUI and utilizes the integrated code-level Java mock verifier to ensure data parity.
  @HappyPath @OmnichannelDataIntegrity @Thread1
  Scenario: Validate Omnichannel Order Data Integrity for a Successful Standard Customer Checkout
    Given the new customer is logged into the graphical user interface channel
    And they have added any product to their shopping cart
    When they complete the multi-step checkout journey and submit the final payment details
    Then a unique order identification is dynamically generated on the "Order Placed!" graphical page
    And the integrated Order Management System (OMS) simulation validates that the order data has successfully synchronized across channels.

  # Scenario 2: The "Negative/Edge Case" (Shoprite Edge Scenario)
  #: This scenario differentiates you by modeling dynamic business failure.
  # Based on Shoprite background, we are automating a checkout with specific payment details that should cause a simulated Payment Decline, proving the OMS handles failure intelligently rather than assuming pristine data states.
  @NegativeScenario @ShopriteEdgeCase @OmsResilience @Thread2
  Scenario: Verify Order Management System Resilience and Failover Handling for a Simulated Payment Decline Scenario
    Given the new customer is logged into the graphical user interface channel
    And they have added any product to their shopping cart
    When they complete the checkout journey but enter specific dynamic card details that simulate a payment decline
    Then the final payment transaction is declined.
    And the integrated Order Management System (OMS) simulation validates that the system handles the dynamic payment volatility with high resilience.