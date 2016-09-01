package org.camunda.bpm.scenario.test.errors;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/errors/EventBasedErrorSubProcessTest.bpmn"})
public class EventBasedErrorSubProcessTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("EventBasedErrorSubProcessTest").execute();

    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventError");

  }

  @Test
  public void testHandleBpmnError() {

    when(scenario.waitsAtServiceTask("ServiceTask")).thenReturn(new ServiceTaskAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.handleBpmnError("errorCode");
      }
    });

    Scenario.run(scenario).startByKey("EventBasedErrorSubProcessTest").execute();

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

  }

}
