package org.camunda.bpm.scenario.test;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ServiceTaskAction;
import org.camunda.bpm.scenario.runner.ServiceTaskWaitstate;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/ServiceTaskTest.bpmn"})
public class ServiceTaskTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.atServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ServiceTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.process("ServiceTaskTest").start(scenario);

    verify(scenario, times(1)).hasCompleted("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.atServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ServiceTaskWaitstate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.process("ServiceTaskTest").start(scenario);

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, never()).hasFinished("ServiceTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.process("ServiceTaskTest").start(scenario);

  }

  @Test
  public void testToBeforeServiceTask() {

    when(scenario.atServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ServiceTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.process("ServiceTaskTest").toBefore("ServiceTask").start(scenario);

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, never()).hasFinished("ServiceTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  public void testToAfterServiceTask() {

    when(scenario.atServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ServiceTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.process("ServiceTaskTest").toAfter("ServiceTask").start(scenario);

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, times(1)).hasFinished("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ServiceTaskWaitstate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.process("ServiceTaskTest").toBefore("ServiceTask").start(otherScenario);
    Scenario.process("ServiceTaskTest").start(scenario);

    verify(scenario, times(1)).hasCompleted("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("ServiceTask");

  }

}
