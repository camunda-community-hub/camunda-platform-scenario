package org.camunda.bpm.scenario.test.singlewaitstate;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.delegate.TimerJobDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
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
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(TimerJobDelegate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
  public void testDoNotDealWithTimerEvent() {

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
  public void testToBeforeTimerIntermediateEvent() {

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").toBefore("TimerIntermediateEvent").execute();

    verify(scenario, times(1)).hasStarted("TimerIntermediateEvent");
    verify(scenario, never()).hasFinished("TimerIntermediateEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
  public void testToAfterTimerIntermediateEvent() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(TimerJobDelegate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateEventTest").toAfter("TimerIntermediateEvent").execute();

    verify(scenario, times(1)).hasStarted("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(TimerJobDelegate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(otherScenario).startBy("TimerIntermediateEventTest").toBefore("TimerIntermediateEvent").execute();
    Scenario.run(scenario).startBy("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("TimerIntermediateEvent");

  }

}
