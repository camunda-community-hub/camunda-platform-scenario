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

  @Given("Having the meal together completes successfully")
  public void HavingTheMealTogetherCompletesSuccessfully() {
    when(process.waitsAtServiceTask("HaveMealTogetherTask"))
      .thenReturn(task -> task.complete());
  }

  @Given("Preparing the meal completes successfully")
  public void PreparingTheMealCompletesSuccessfully() {
    when(process.waitsAtServiceTask("PrepareMealTask"))
      .thenReturn(task -> task.complete());
  }

  @Given("Preparing the meal fails because ingredients are missing")
  public void preparingTheMealFailsBecauseOfMissingIngredients() {
    when(process.waitsAtServiceTask("PrepareMealTask"))
      .thenReturn(task -> task.handleBpmnError("IngredientsAreMissingError"));
  }

  @When("the meal is upcoming")
  @Deployment(resources = "org/camunda/bpm/scenario/test/cucumber/DinnerWithCucumber.bpmn")
  public void theMealIsUpcoming() {
    run(process).startByKey("DinnerWithCucumber").execute();
  }

  @Then("preparing the meal will complete")
  public void theMealWillBePrepared() {
    verify(process, times(1)).hasCompleted("PrepareMealTask");
  }

  @Then("preparing the meal will not complete")
  public void preparingTheMailWillNotComplete() {
    verify(process, never()).hasCompleted("PrepareMealTask");
  }

  @Then("having the meal together will complete")
  public void havingTheMealTogetherWillComplete() {
    verify(process, times(1)).hasFinished("HaveMealTogetherTask");
  }

  @Then("having the meal together will not start")
  public void havingTheMealTogetherWillNotStart() {
    verify(process, never()).hasStarted("HaveMealTogetherTask");
  }

  @Then("the meal is finished")
  public void theMealIsFinished() {
    verify(process, times(1)).hasFinished("MealIsFinishedEndEvent");
  }

  @Then("the meal is prepared")
  public void theMealIsPrepared() {
    verify(process, times(1)).hasFinished("MealIsPreparedEvent");
  }

  @Then("the meal is not prepared")
  public void theMealIsNotPrepared() {
    verify(process, times(1)).hasFinished("MealIsNotPreparedEndEvent");
  }

}
