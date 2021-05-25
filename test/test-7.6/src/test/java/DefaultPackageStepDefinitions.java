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
public class DefaultPackageStepDefinitions {

  ProcessScenario process = mock(ProcessScenario.class);

  @Given("all default activities not explicitly mentioned complete successfully")
  public void given_all_activities_not_explicitly_mentioned_complete_successfully() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.complete());
    when(process.waitsAtServiceTask("HaveMealTogether"))
      .thenReturn(task -> task.complete());
  }

  @Given("default ingredients are missing")
  public void given_ingredients_are_missing() {
    when(process.waitsAtServiceTask("PrepareMeal"))
      .thenReturn(task -> task.handleBpmnError("IngredientsMissing"));
  }

  @When("a default meal is upcoming")
  @Deployment(resources = "DefaultPackageCucumberTest.bpmn")
  public void when_a_meal_is_upcoming() {
    run(process).startByKey("DefaultPackageCucumberTest").execute();
  }

  @Then("the default meal is prepared")
  public void then_the_meal_is_prepared() {
    verify(process, times(1)).hasFinished("MealPrepared");
    verify(process, never()).hasFinished("MealNotPrepared");
  }

  @Then("the default meal is not prepared")
  public void then_the_meal_is_not_prepared() {
    verify(process, times(1)).hasFinished("MealNotPrepared");
    verify(process, never()).hasFinished("MealPrepared");
  }

  @Then("we have default meal together")
  public void then_we_have_meal_together() {
    verify(process, times(1)).hasFinished("HaveMealTogether");
  }

  @Then("we don't have default meal together")
  public void then_we_dont_have_meal_together() {
    verify(process, never()).hasFinished("HaveMealTogether");
  }

  @Then("the default meal is finished")
  public void then_the_meal_is_finished() {
    verify(process, times(1)).hasFinished("MealFinished");
  }

}
