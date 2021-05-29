Feature: Dinner
  We are going to prepare and have dinner together

  Background:
    * Preparing the meal completes successfully
    * Having the meal together completes successfully

  Scenario: Happy dinner

    When the meal is upcoming
    Then preparing the meal will complete
    And having the meal together will complete
    And the meal is finished

  Scenario: Ingredients are missing
    * Preparing the meal fails because ingredients are missing

    When the meal is upcoming
    Then preparing the meal will not complete
    And the meal is not prepared
    And having the meal together will not start
