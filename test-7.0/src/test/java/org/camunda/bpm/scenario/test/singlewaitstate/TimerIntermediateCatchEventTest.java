package org.camunda.bpm.scenario.test.singlewaitstate;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.TimerIntermediateCatchEventAction;
import org.camunda.bpm.scenario.runner.TimerIntermediateCatchEventWaitstate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testTriggerTimer() {

    when(scenario.atTimerIntermediateCatchEvent("TimerIntermediateCatchEvent")).thenReturn(new TimerIntermediateCatchEventAction() {
      @Override
      public void execute(TimerIntermediateCatchEventWaitstate timer) {
        timer.getManagementService().executeJob(timer.getId()); // normally not necessary for timers, but allowed ...
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.atTimerIntermediateCatchEvent("TimerIntermediateCatchEvent")).thenReturn(new TimerIntermediateCatchEventAction() {
      @Override
      public void execute(TimerIntermediateCatchEventWaitstate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testDoNotDealWithTimerEvent() {

    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testToBeforeTimerIntermediateCatchEvent() {

    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").toBefore("TimerIntermediateCatchEvent").execute();

    verify(scenario, times(1)).hasStarted("TimerIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testToAfterTimerIntermediateCatchEvent() {

    when(scenario.atTimerIntermediateCatchEvent("TimerIntermediateCatchEvent")).thenReturn(new TimerIntermediateCatchEventAction() {
      @Override
      public void execute(TimerIntermediateCatchEventWaitstate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").toAfter("TimerIntermediateCatchEvent").execute();

    verify(scenario, times(1)).hasStarted("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/TimerIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atTimerIntermediateCatchEvent("TimerIntermediateCatchEvent")).thenReturn(new TimerIntermediateCatchEventAction() {
      @Override
      public void execute(TimerIntermediateCatchEventWaitstate timer) {
        // Do nothing means process moves forward here
      }
    });

    Scenario.run(otherScenario).startBy("TimerIntermediateCatchEventTest").toBefore("TimerIntermediateCatchEvent").execute();
    Scenario.run(scenario).startBy("TimerIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("TimerIntermediateCatchEvent");

  }

}
