package org.camunda.bpm.scenario.test.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.ProcessScenario;

import static org.camunda.bpm.scenario.Scenario.run;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
public class DinnerStepDefinitions {

  ProcessScenario testProcess = mock(ProcessScenario.class);

  @Given("all activities not explicitly mentioned complete successfully")
  public void given_all_activities_not_explicitly_mentioned_complete_successfully() {
    when(testProcess.waitsAtServiceTask("PrepareMeal")).thenReturn(task -> {
        task.complete();
      }
    );
    when(testProcess.waitsAtServiceTask("HaveMealTogether")).thenReturn(task -> {
        task.complete();
      }
    );
  }

  @Given("ingredients are missing")
  public void given_ingredients_are_missing() {
    when(testProcess.waitsAtServiceTask("PrepareMeal")).thenReturn(task -> {
        task.handleBpmnError("IngredientsMissing");
      }
    );
  }

  @When("a meal is upcoming")
  @Deployment(resources = "org/camunda/bpm/scenario/test/cucumber/Dinner.bpmn")
  public void when_a_meal_is_upcoming() {
    run(testProcess).startByKey("Dinner").execute();
  }

  @Then("the meal is prepared")
  public void then_the_meal_is_prepared() {
    verify(testProcess, times(1)).hasFinished("MealPrepared");
    verify(testProcess, never()).hasFinished("MealNotPrepared");
  }

  @Then("the meal is not prepared")
  public void then_the_meal_is_not_prepared() {
    verify(testProcess, times(1)).hasFinished("MealNotPrepared");
    verify(testProcess, never()).hasFinished("MealPrepared");
  }

  @Then("we have meal together")
  public void then_we_have_meal_together() {
    verify(testProcess, times(1)).hasFinished("HaveMealTogether");
  }

  @Then("we don't have meal together")
  public void then_we_dont_have_meal_together() {
    verify(testProcess, never()).hasFinished("HaveMealTogether");
  }

  @Then("the meal is finished")
  public void then_the_meal_is_finished() {
    verify(testProcess, times(1)).hasFinished("MealFinished");
  }

}
