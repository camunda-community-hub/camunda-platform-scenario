Feature: Dinner
  We are going to have a happy meal today

  Background:
    Given all activities not explicitly mentioned complete successfully

  Scenario: Happy meal together
    When a meal is upcoming
    Then the meal is prepared
    And we have meal together
    And the meal is finished

  Scenario: Ingredients are missing
    Given ingredients are missing
    When a meal is upcoming
    Then the meal is not prepared
    And we don't have meal together
