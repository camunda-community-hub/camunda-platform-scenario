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
public class MultipleCallActivitiesChildMockingTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/callactivities/MultipleCallActivitiesTest.bpmn"})
  public void testCompleteCallActivities() {

    when(scenario.waitsAtMockedCallActivity("CallActivity1")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    when(scenario.waitsAtMockedCallActivity("CallActivity2")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    when(scenario.waitsAtMockedCallActivity("CallActivity3")).thenReturn(new MockedCallActivityAction() {
      @Override
      public void execute(MockedCallActivityDelegate callActivity) {
        callActivity.complete();
      }
    });

    Scenario.run(scenario)
      .withMockedProcess("Child1")
      .withMockedProcess("Child2")
      .startByKey("MultipleCallActivitiesTest")
      .execute();

    verify(scenario, times(1)).hasFinished("CallActivity1");
    verify(scenario, times(1)).hasFinished("CallActivity2");
    verify(scenario, times(1)).hasFinished("CallActivity3");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
