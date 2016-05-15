Feature: Predefined Paths
    Provide users with set of predefined JSONPath patterns to choose for the step output fields

  Scenario Outline: Select Jira API
    When <User> selects <Jira API> in API tab
    Then <User> should get appropriate set of predefined paths for the selected <Jira API>

    Examples: 
      | Jira API | Field     | Pattern                          |
      | search   | "key"     | "$.issues[*].key"                |
      |          | "summary" | "$.issues[*].fields.summary"     |
      |          | "status"  | "$.issues[*].fields.status.name" |

  @pjp-46
  Scenario: Context predefined field patterns
    When I select a Jira API from the API selection drop down
    Then I should be able to see predefined JSONPath patterns into Output Fields section
    When I select a predefined field
    Then The related patterns should be loaded into the JSON Path column
    When I select predefined JSON Path patternt
    Then the related field name should be loaded into Field column
