package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.ReceiveTaskAction;
import org.camunda.bpm.scenario.delegate.MessageEventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class ReceiveTaskTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.actsOnReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasFinished("ReceiveTask");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        // Deal with messageEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startBy("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasStarted("ReceiveTask");
    verify(scenario, never()).hasFinished("ReceiveTask");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testDoNotDealWithMessageEvent() {

    Scenario.run(scenario).startBy("ReceiveTaskTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/ReceiveTaskTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
      }
    });

    when(otherScenario.actsOnReceiveTask("ReceiveTask")).thenReturn(new ReceiveTaskAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startBy("ReceiveTaskTest").execute();
    Scenario.run(scenario).startBy("ReceiveTaskTest").execute();

    verify(scenario, times(1)).hasFinished("ReceiveTask");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("ReceiveTask");

  }

}
