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

  @Given("Unmentioned activities complete successfully")
  public void assume_things_basically_work_out_fine() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.complete());
    when(process.waitsAtServiceTask("HaveMealTogether"))
      .thenReturn(task -> task.complete());
  }

  @Given("The meal preparation fails because of missing ingredients")
  public void the_meal_preparation_fails_because_of_missing_ingredients() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.handleBpmnError("IngredientsMissing"));
  }

  @When("a meal is upcoming")
  @Deployment(resources = "org/camunda/bpm/scenario/test/cucumber/DinnerWithCucumber.bpmn")
  public void when_a_meal_is_upcoming() {
    run(process).startByKey("DinnerWithCucumber").execute();
  }

  @Then("the meal will be prepared")
  public void then_the_meal_will_be_prepared() {
    verify(process, times(1)).hasFinished("MealPrepared");
    verify(process, never()).hasFinished("MealNotPrepared");
  }

  @Then("the meal will not be prepared")
  public void then_the_meal_will_not_be_prepared() {
    verify(process, times(1)).hasFinished("MealNotPrepared");
    verify(process, never()).hasFinished("MealPrepared");
  }

  @Then("we will have meal together")
  public void then_we_will_have_meal_together() {
    verify(process, times(1)).hasFinished("HaveMealTogether");
  }

  @Then("we will not have meal together")
  public void then_we_dont_have_meal_together() {
    verify(process, never()).hasFinished("HaveMealTogether");
  }

  @Then("the meal will be finished")
  public void then_the_meal_will_be_finished() {
    verify(process, times(1)).hasFinished("MealFinished");
  }

}
