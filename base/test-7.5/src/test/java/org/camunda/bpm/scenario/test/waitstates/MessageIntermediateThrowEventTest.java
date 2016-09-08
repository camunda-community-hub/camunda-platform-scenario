package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MessageIntermediateThrowEventAction;
import org.camunda.bpm.scenario.delegate.ExternalTaskDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
@Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateThrowEventTest.bpmn"})
public class MessageIntermediateThrowEventTest extends AbstractTest {

  @Test
  public void testCompleteTask() {

    when(scenario.waitsAtMessageIntermediateThrowEvent("MessageIntermediateThrowEvent")).thenReturn(new MessageIntermediateThrowEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    Scenario.run(scenario).startByKey("MessageIntermediateThrowEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageIntermediateThrowEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  public void testDoNothing() {

    when(scenario.waitsAtMessageIntermediateThrowEvent("MessageIntermediateThrowEvent")).thenReturn(new MessageIntermediateThrowEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        // Deal with externalTask but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("MessageIntermediateThrowEventTest").execute();

    verify(scenario, times(1)).hasStarted("MessageIntermediateThrowEvent");
    verify(scenario, never()).hasFinished("MessageIntermediateThrowEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  public void testDoNotDealWithTask() {

    Scenario.run(scenario).startByKey("MessageIntermediateThrowEventTest").execute();

  }

  @Test
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtMessageIntermediateThrowEvent("MessageIntermediateThrowEvent")).thenReturn(new MessageIntermediateThrowEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
        externalTask.complete();
      }
    });

    when(otherScenario.waitsAtMessageIntermediateThrowEvent("MessageIntermediateThrowEvent")).thenReturn(new MessageIntermediateThrowEventAction() {
      @Override
      public void execute(ExternalTaskDelegate externalTask) {
      }
    });

    Scenario.run(otherScenario).startByKey("MessageIntermediateThrowEventTest").execute();
    Scenario.run(scenario).startByKey("MessageIntermediateThrowEventTest").execute();

    verify(scenario, times(1)).hasCompleted("MessageIntermediateThrowEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasCompleted("MessageIntermediateThrowEvent");

  }

}
