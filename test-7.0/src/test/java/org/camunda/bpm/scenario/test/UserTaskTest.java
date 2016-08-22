package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.UserTaskAction;
import org.camunda.bpm.scenario.runner.UserTaskWaitstate;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class UserTaskTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.process("UserTaskTest").execute(scenario);

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testDoNothing() {

    when(scenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        // Deal with task but do nothing here
      }
    });

    Scenario.process("UserTaskTest").execute(scenario);

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.process("UserTaskTest").execute(scenario);

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testToBeforeUserTask() {

    when(scenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.process("UserTaskTest").toBefore("UserTask").execute(scenario);

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, never()).hasFinished("UserTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testToAfterUserTask() {

    when(scenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.process("UserTaskTest").toAfter("UserTask").execute(scenario);

    verify(scenario, times(1)).hasStarted("UserTask");
    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/UserTaskTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atUserTask("UserTask")).thenReturn(new UserTaskAction() {
      @Override
      public void execute(UserTaskWaitstate task) {
        task.complete();
      }
    });

    Scenario.process("UserTaskTest").toBefore("UserTask").execute(otherScenario);
    Scenario.process("UserTaskTest").execute(scenario);

    verify(scenario, times(1)).hasFinished("UserTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("UserTask");

  }

}
