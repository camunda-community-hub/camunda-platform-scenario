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
public class DinnerWithCucumberStepDefinitions {

  ProcessScenario process = mock(ProcessScenario.class);

  @Given("Things will work out fine")
  public void assume_things_basically_work_out_fine() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.complete());
    when(process.waitsAtServiceTask("HaveMealTogether"))
      .thenReturn(task -> task.complete());
  }

  @Given("Ingredients will be missing")
  public void given_ingredients_are_missing() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.handleBpmnError("IngredientsMissing"));
  }

  @When("a meal is upcoming")
  @Deployment(resources = "org/camunda/bpm/scenario/test/cucumber/DinnerWithCucumber.bpmn")
  public void when_a_meal_is_upcoming() {
    run(process).startByKey("DinnerWithCucumber").execute();
  }

  @Then("the meal is prepared")
  public void then_the_meal_is_prepared() {
    verify(process, times(1)).hasFinished("MealPrepared");
    verify(process, never()).hasFinished("MealNotPrepared");
  }

  @Then("the meal is not prepared")
  public void then_the_meal_is_not_prepared() {
    verify(process, times(1)).hasFinished("MealNotPrepared");
    verify(process, never()).hasFinished("MealPrepared");
  }

  @Then("we have meal together")
  public void then_we_have_meal_together() {
    verify(process, times(1)).hasFinished("HaveMealTogether");
  }

  @Then("we don't have meal together")
  public void then_we_dont_have_meal_together() {
    verify(process, never()).hasFinished("HaveMealTogether");
  }

  @Then("the meal is finished")
  public void then_the_meal_is_finished() {
    verify(process, times(1)).hasFinished("MealFinished");
  }

}
