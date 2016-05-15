Feature: Custom Call
    Advanced JIRA API Users are profided with interface to make their customised calls to JIRA instance and process 
    responses

  @pjp-53
  Scenario: Make a custom call
    When I select 'Custom' API call from the drop down
    And I provide URI for some Jira REST API
    And I provide correct output fields in form of JSONPath patterns
    Then I should be able to receive Jira data from the call into the next steps
