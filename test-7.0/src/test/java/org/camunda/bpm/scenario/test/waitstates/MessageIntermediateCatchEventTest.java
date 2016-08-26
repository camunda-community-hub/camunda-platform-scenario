package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.MessageIntermediateCatchEventAction;
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
public class MessageIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
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
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
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
  public void testToBeforeMessageIntermediateCatchEvent() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").toBefore("MessageIntermediateCatchEvent").execute();

    verify(scenario, times(1)).hasStarted("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testToAfterMessageIntermediateCatchEvent() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").toAfter("MessageIntermediateCatchEvent").execute();

    verify(scenario, times(1)).hasStarted("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(MessageEventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receiveMessage();
      }
    });

    Scenario.run(otherScenario).startBy("MessageIntermediateCatchEventTest").toBefore("MessageIntermediateCatchEvent").execute();
    Scenario.run(scenario).startBy("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("MessageIntermediateCatchEvent");

  }

}
