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
public class CallActivityChildMockingTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn"})
  public void testCompleteCallActivity() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario)
      .withMockedProcess("Child")
      .startByKey("CallActivityTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn"
  })
  public void testDoNothing() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityTest").execute();

    verify(scenario, times(1)).hasStarted("CallActivity");
    verify(scenario, never()).hasFinished("CallActivity");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn"
  })
  public void testDoNotDealWithCallActivity() {

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityTest").execute();

  }

  @Test(expected = AssertionError.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn",
    "org/camunda/bpm/scenario/test/callactivities/Child.bpmn"
  })
  public void testDoMockPresentCallActivity() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).withMockedProcess("Child").startByKey("CallActivityTest").execute();

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn"
  })
  public void testDoNotMockCallActivity() {

    when(scenario.waitsAtMockedCallActivity("CallActivity")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        // Deal with task but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("CallActivityTest").execute();

  }

  @Test
  @Deployment(resources = {
    "org/camunda/bpm/scenario/test/callactivities/CallActivityTest.bpmn"
  })
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
      }
    });

    Scenario
      .run(scenario).withMockedProcess("Child").startByKey("CallActivityTest")
      .run(otherScenario).withMockedProcess("Child").startByKey("CallActivityTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, times(1)).hasStarted("CallActivity");
    verify(otherScenario, never()).hasFinished("CallActivity");

  }

}
