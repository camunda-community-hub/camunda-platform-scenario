Feature: Dinner
  We are going to have a happy meal today

  Background:
    Given all default activities not explicitly mentioned complete successfully

  Scenario: Happy meal together
    When a default meal is upcoming
    Then the default meal is prepared
    And we have default meal together
    And the default meal is finished

  Scenario: Ingredients are missing
    Given default ingredients are missing
    When a default meal is upcoming
    Then the default meal is not prepared
    And we don't have default meal together
