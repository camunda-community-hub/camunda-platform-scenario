import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.scenario.ProcessScenario;
import org.camunda.bpm.scenario.report.junit.ProcessEngineRuleWithReporting;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.scenario.Scenario.run;
import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak
 */
@Deployment(resources = {"Dinner.bpmn"})
public class DinnerTest {

  ProcessScenario process = mock(ProcessScenario.class);

  @Rule
  public ProcessEngineRule processEngineRule = new ProcessEngineRuleWithReporting();

  @Before
  public void everything_works_out_as_expected() {
    when(process.waitsAtServiceTask("PrepareDinner"))
      .thenReturn(task -> task.complete());
    when(process.waitsAtUserTask("HaveDinnerTogether"))
      .thenReturn(task -> task.complete());
  }

  @Test
  public void happy_dinner_together() {
    when_a_dinner_is_upcoming();
    then_the_dinner_is_prepared();
    then_we_have_dinner_together();
    then_the_dinner_is_finished();
  }

  @Test
  public void ingredients_are_missing() {
    given_ingredients_are_missing();
    when_a_dinner_is_upcoming();
    then_the_dinner_is_not_prepared();
    then_we_dont_have_dinner_together();
  }

  public void given_ingredients_are_missing() {
    when(process.waitsAtServiceTask("PrepareDinner"))
      .thenReturn(task -> task.handleBpmnError("IngredientsMissing"));
  }

  public void when_a_dinner_is_upcoming() {
    run(process).startByKey("Dinner2").execute();
  }

  public void then_the_dinner_is_prepared() {
    verify(process, times(1)).hasFinished("DinnerPrepared");
    verify(process, never()).hasFinished("DinnerNotPrepared");
  }

  public void then_the_dinner_is_not_prepared() {
    verify(process, times(1)).hasFinished("DinnerNotPrepared");
    verify(process, never()).hasFinished("DinnerPrepared");
  }

  public void then_we_have_dinner_together() {
    verify(process, times(1)).hasFinished("HaveDinnerTogether");
  }

  public void then_we_dont_have_dinner_together() {
    verify(process, never()).hasFinished("HaveDinnerTogether");
  }

  public void then_the_dinner_is_finished() {
    verify(process, times(1)).hasFinished("DinnerFinished");
  }

}
