package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testReceiveSignal() {

    when(scenario.waitsAtSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate signalEventSubscription) {
        signalEventSubscription.receive();
      }
    });

    Scenario.run(scenario).startByKey("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate signalEventSubscription) {
        // Deal with signalEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startByKey("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasStarted("SignalIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testDoNotDealWithSignalEvent() {

    Scenario.run(scenario).startByKey("SignalIntermediateCatchEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.waitsAtSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate signalEventSubscription) {
        signalEventSubscription.receive();
      }
    });

    when(otherScenario.waitsAtSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(EventSubscriptionDelegate signalEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startByKey("SignalIntermediateCatchEventTest").execute();
    Scenario.run(scenario).startByKey("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("SignalIntermediateCatchEvent");

  }

}
