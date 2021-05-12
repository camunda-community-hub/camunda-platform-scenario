package org.camunda.bpm.scenario.test.callactivities;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MockedCallActivityAction;
import org.camunda.bpm.scenario.delegate.MockedCallActivityDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class CallActivityWithBoundaryEscalationEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryEscalationEventTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityWithBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryEscalationEventTest.bpmn"})
  public void testEscalateNonInterrupting() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(final MockedCallActivityDelegate callActivity) {
        callActivity.handleEscalation("escNonInterrupting");
        callActivity.complete();
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityWithBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventInterrupted");
    verify(scenario, times(1)).hasFinished("EndEventNotInterrupted");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryEscalationEventTest.bpmn"})
  public void testEscalateInterrupting() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(final MockedCallActivityDelegate callActivity) {
        callActivity.handleEscalation("escInterrupting");
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityWithBoundaryEscalationEventTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasCompleted("CallActivity");
    verify(scenario, times(1)).hasCanceled("CallActivity");

    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventInterrupted");
    verify(scenario, never()).hasFinished("EndEventNotInterrupted");

  }

}
