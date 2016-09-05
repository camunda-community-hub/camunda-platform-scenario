package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.TimerIntermediateEventAction;
import org.camunda.bpm.scenario.defer.Deferred;
import org.camunda.bpm.scenario.delegate.ProcessInstanceDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class TimerIntermediateEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testTriggerTimer() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate processInstance) {
        Job job = rule.getManagementService().createJobQuery().processInstanceId(processInstance.getId()).singleResult();
        rule.getManagementService().executeJob(job.getId()); // not encouraged for timers, but possible and tested here...
      }
    });

    Scenario.run(scenario).startByKey("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(ProcessInstanceDelegate timer) {
        // Deal with timerEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario)
        .startByKey("TimerIntermediateEventTest")
        .execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoSomethingDeferred() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(final ProcessInstanceDelegate pi) {
        pi.defer("PT3M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // expected
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("TimerIntermediateEventTest").execute();

  }

  @Test(expected = Exception.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoSomethingDeferredToExactlyTheTimer() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(final ProcessInstanceDelegate pi) {
        pi.defer("PT5M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // expected
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("TimerIntermediateEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoSomethingTooLate() {

    when(scenario.waitsAtTimerIntermediateEvent("TimerIntermediateEvent")).thenReturn(new TimerIntermediateEventAction() {
      @Override
      public void execute(final ProcessInstanceDelegate pi) {
        pi.defer("PT8M", new Deferred() {
          @Override
          public void execute() throws Exception {
            throw new Exception(); // not expected
          }
        });
      }
    });

    Scenario.run(scenario).startByKey("TimerIntermediateEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/TimerIntermediateEventTest.bpmn"})
  public void testDoNotDealWithTimerEvent() {

    Scenario.run(scenario).startByKey("TimerIntermediateEventTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

}
