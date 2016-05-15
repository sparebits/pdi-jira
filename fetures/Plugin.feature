Feature: Jira Plugin

  @pjp-34
  Scenario: Get help information
    When I select Help button in the plugin UI
    Then I should be able to go to plugin web site and read the documentation for the plugin

  @pjp-55
  Scenario: Provide types for output fields
    Given User has set an output field
    And pattern for the output field
    When User selects type for the output field
    Then output field type should be persisted into transformation
    And output field should be provided to next steps with appropriate value type
