package org.camunda.bpm.scenario.test.processes;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.UserTaskAction;
import org.camunda.bpm.scenario.delegate.TaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class NonBlockingSubProcessTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingSubProcessTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    Scenario.run(scenario).startByKey("NonBlockingSubProcessTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(2)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingSubProcessTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("NonBlockingSubProcessTest").execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingSubProcessTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("NonBlockingSubProcessTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/processes/NonBlockingSubProcessTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
        task.complete();
      }
    });

    when(otherScenario.waitsAtUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(TaskDelegate task) {
      }
    });

    Scenario
      .run(scenario).startByKey("NonBlockingSubProcessTest")
      .run(otherScenario).startByKey("NonBlockingSubProcessTest")
      .execute();

    verify(scenario, times(1)).hasFinished("SubProcess");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(2)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasFinished("SubProcess");
    verify(otherScenario, never()).hasFinished("UserTask");
    verify(otherScenario, times(1)).hasFinished("EndEvent");

  }

}
