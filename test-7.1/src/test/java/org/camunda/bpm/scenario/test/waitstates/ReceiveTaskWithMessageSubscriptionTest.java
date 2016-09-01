package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.ReceiveTaskAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskWithMessageSubscriptionTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    Scenario.run(scenario).startByKey("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasFinished("ReceiveTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        // Deal with messageEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasStarted("ReceiveTask");
    verify(scenario, never()).hasFinished("ReceiveTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testDoNotDealWithMessageEvent() {

    Scenario.run(scenario).startByKey("ReceiveTaskTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    when(otherScenario.waitsAtReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startByKey("ReceiveTaskTest").execute();
    Scenario.run(scenario).startByKey("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasFinished("ReceiveTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("ReceiveTask");

  }

}
