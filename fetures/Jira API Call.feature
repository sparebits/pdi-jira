@pjp-41
Feature: Jira API Call

  @pjp-51
  Scenario: Project API call
    When I select 'Project' API call from the API selection drop down
    And I provide URI for some Jira REST API
    And I provide project key (case insensitive)
    Then I should be able to receive Jira data from the call into the next steps

  @pjp-49
  Scenario: Wrong URI
    When I make a call to non existing item (issue, project etc.)
    Then I should get HTTP 404 (not found) response
    And transformation should stop with error

  @pjp-44 @pjp-42
  Scenario: Issue API sub-call
    When I select 'Issue' API from the drop down
    Then I should be able to select a subcall to the API like 'Comments' or 'Worklog'
    And I should be able to receive Jira data from the call into the next steps
