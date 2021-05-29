Feature: Dinner
  We are going to prepare and have dinner together

  Scenario: Happy dinner
    * Things will work out fine

    When a meal is upcoming
    Then the meal is prepared
    And we have meal together
    And the meal is finished

  Scenario: Ingredients missing
    * Things will work out fine
    * Ingredients will be missing

    When a meal is upcoming
    Then the meal is not prepared
    And we don't have meal together
