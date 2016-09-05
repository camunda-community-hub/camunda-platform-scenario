package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.MessageIntermediateCatchEventAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class MessageIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.waitsAtMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    Scenario.run(scenario).startByKey("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        // Deal with messageEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasStarted("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testDoNotDealWithMessageEvent() {

    Scenario.run(scenario).startByKey("MessageIntermediateCatchEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/MessageIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
        messageEventSubscription.receive();
      }
    });

    when(otherScenario.waitsAtMessageIntermediateCatchEvent("MessageIntermediateCatchEvent")).thenReturn(new MessageIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate messageEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startByKey("MessageIntermediateCatchEventTest").execute();
    Scenario.run(scenario).startByKey("MessageIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("MessageIntermediateCatchEvent");

  }

}
