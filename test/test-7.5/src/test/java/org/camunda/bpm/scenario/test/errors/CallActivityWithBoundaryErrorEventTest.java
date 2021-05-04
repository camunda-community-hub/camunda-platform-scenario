package org.camunda.bpm.scenario.test.errors;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MockedCallActivityAction;
import org.camunda.bpm.scenario.act.ServiceTaskAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/errors/CallActivityWithBoundaryErrorEventTest.bpmn"})
public class CallActivityWithBoundaryErrorEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventError");

  }

  @Test
  public void testHandleBpmnError() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.handleBpmnError("errorCode");
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryErrorEventTest").execute();

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventError");

  }

}
