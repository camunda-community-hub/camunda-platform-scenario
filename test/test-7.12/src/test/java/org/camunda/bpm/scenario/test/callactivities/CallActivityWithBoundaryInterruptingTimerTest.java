package org.camunda.bpm.scenario.test.callactivities;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MockedCallActivityAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.MockedCallActivityDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public class CallActivityWithBoundaryInterruptingTimerTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testCompleteTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtMockedCallActivity("CallActivity");
    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventCanceled");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testExactlyReachingMaximalTimeForTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(final MockedCallActivityDelegate callActivity) {
        callActivity.defer("PT5M", new Deferred() {
          @Override
          public void execute() {
            // do nothing
          }
        });
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtMockedCallActivity("CallActivity");
    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventCanceled");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testTakeMuchTooLongForTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(final MockedCallActivityDelegate callActivity) {
        callActivity.defer("PT6M", new Deferred() {
          @Override
          public void execute() {
            callActivity.complete();
          }
        });
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventCanceled");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testTakeABitTimeForTask() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(final MockedCallActivityDelegate callActivity) {
        callActivity.defer("PT4M", new Deferred() {
          @Override
          public void execute() {
            callActivity.complete();
          }
        });
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtMockedCallActivity("CallActivity");
    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventCanceled");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

    verify(scenario, times(1)).waitsAtMockedCallActivity("CallActivity");
    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEventCompleted");
    verify(scenario, times(1)).hasFinished("EndEventCanceled");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityWithBoundaryInterruptingTimerTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    when(otherScenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario
      .run(otherScenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest")
      .run(scenario).withMockedProcess("Child").startByKey("BoundaryInterruptingTimerTest")
      .execute();

    verify(scenario, times(1)).waitsAtMockedCallActivity("CallActivity");
    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEventCompleted");
    verify(scenario, never()).hasFinished("EndEventCanceled");

  }

}
