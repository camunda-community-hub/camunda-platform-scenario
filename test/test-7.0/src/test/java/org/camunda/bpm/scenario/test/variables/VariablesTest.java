package org.camunda.bpm.scenario.test.variables;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;
import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class VariablesTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testNoVariables() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testVariableSetAtInstanceLevelSuccess() {

    variables.put("globalVariable", "globalVariableValue");

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        assertThat(task.getVariables()).containsEntry("globalVariable", "globalVariableValue");
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest", variables).execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/variables/VariablesTest.bpmn"})
  public void testVariableSetAtInstanceLevelFailure() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        assertThat(task.getVariables()).hasSize(1)
            .containsEntry("globalVariable", "globalVariableValue");
        task.complete(withVariables("localVariable", "localVariableValue"));
        assertThat(task.getVariables()).hasSize(2)
            .containsEntry("localVariable", "localVariableValue")
            .containsEntry("globalVariable", "globalVariableValue");
        assertThat(task.getProcessInstance().getVariables()).hasSize(1)
            .containsEntry("globalVariable", "globalVariableValue");
      }
    });

    Scenario.run(scenario).startByKey("VariablesTest", variables).execute();

  }

}
