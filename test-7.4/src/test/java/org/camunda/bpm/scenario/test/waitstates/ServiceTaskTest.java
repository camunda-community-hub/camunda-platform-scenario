package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ServiceTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ServiceTaskTest.bpmn"})
public class ServiceTaskTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.actsOnServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("ServiceTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.actsOnServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startBy("ServiceTaskTest").execute();

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, never()).hasFinished("ServiceTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startBy("ServiceTaskTest").execute();

  }

  @Test
  public void testToBeforeServiceTask() {

    when(scenario.actsOnServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("ServiceTaskTest").toBefore("ServiceTask").execute();

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, never()).hasFinished("ServiceTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  public void testToAfterServiceTask() {

    when(scenario.actsOnServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startBy("ServiceTaskTest").toAfter("ServiceTask").execute();

    verify(scenario, times(1)).hasStarted("ServiceTask");
    verify(scenario, times(1)).hasFinished("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(otherScenario).startBy("ServiceTaskTest").toBefore("ServiceTask").execute();
    Scenario.run(scenario).startBy("ServiceTaskTest").execute();

    verify(scenario, times(1)).hasCompleted("ServiceTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("ServiceTask");

  }

}
