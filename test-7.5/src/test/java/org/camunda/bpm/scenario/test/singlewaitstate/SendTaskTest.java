package org.camunda.bpm.scenario.test.singlewaitstate;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.SendTaskAction;
import org.camunda.bpm.scenario.runner.SendTaskWaitstate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/SendTaskTest.bpmn"})
public class SendTaskTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.atSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(SendTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("SendTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("SendTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.atSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(SendTaskWaitstate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startBy("SendTaskTest").execute();

    verify(scenario, times(1)).hasStarted("SendTask");
    verify(scenario, never()).hasFinished("SendTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startBy("SendTaskTest").execute();

  }

  @Test
  public void testToBeforeSendTask() {

    when(scenario.atSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(SendTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("SendTaskTest").toBefore("SendTask").execute();

    verify(scenario, times(1)).hasStarted("SendTask");
    verify(scenario, never()).hasFinished("SendTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  public void testToAfterSendTask() {

    when(scenario.atSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(SendTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("SendTaskTest").toAfter("SendTask").execute();

    verify(scenario, times(1)).hasStarted("SendTask");
    verify(scenario, times(1)).hasFinished("SendTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atSendTask("SendTask")).thenReturn(new SendTaskAction() {
      @Override
      public void execute(SendTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(otherScenario).startBy("SendTaskTest").toBefore("SendTask").execute();
    Scenario.run(scenario).startBy("SendTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("SendTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("SendTask");

  }

}
