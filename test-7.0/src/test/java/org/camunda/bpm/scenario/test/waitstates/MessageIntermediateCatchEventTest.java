package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class MessageIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        // Deal with messageEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasStarted("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testDoNotDealWithMessageEvent() {

    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    when(otherScenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startBy("MessageIntermediateCatchEventTest").execute();
    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("MessageIntermediateCatchEvent");

  }

}
