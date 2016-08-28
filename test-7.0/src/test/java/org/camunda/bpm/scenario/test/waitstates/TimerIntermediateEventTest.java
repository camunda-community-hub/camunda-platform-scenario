package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.assertions.ProcessEngineTests;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.delegate.TimerJobDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testTriggerTimer() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(TimerJobDelegate timer) {
        rule.getManagementService().executeJob(timer.getId()); // normally not necessary for timers, but allowed ...
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(TimerJobDelegate timer) {
        // Deal with timerEventSubscription but do nothing here
      }
    });

    ProcessInstance pi = Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoNotDealWithTimerEvent() {

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

  }

}
