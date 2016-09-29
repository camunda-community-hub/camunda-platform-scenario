package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MessageEndEventAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageEndEventTest.bpmn"})
public class MessageEndEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageEndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasStarted("MessageEndEvent");
    verify(scenario, never()).hasFinished("MessageEndEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    when(otherScenario.waitsAtMessageEndEvent("MessageEndEvent")).thenReturn(new MessageEndEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
      }
    });

    Scenario.run(otherScenario).startByKey("MessageEndEventTest").execute();
    Scenario.run(scenario).startByKey("MessageEndEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageEndEvent");
    verify(otherScenario, never()).hasCompleted("MessageEndEvent");

  }

}
