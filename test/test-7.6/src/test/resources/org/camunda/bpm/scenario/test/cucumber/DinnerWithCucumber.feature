Feature: Dinner
  We are going to prepare and have dinner together

  Background:
    * Unmentioned activities complete successfully

  Scenario: Happy dinner

    When a meal is upcoming
    Then the meal will be prepared
    And we will have meal together
    And the meal will be finished

  Scenario: Ingredients are missing
    * The meal preparation fails because of missing ingredients

    When a meal is upcoming
    Then the meal will not be prepared
    And we will not have meal together
