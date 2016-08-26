package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.SignalIntermediateCatchEventAction;
import org.camunda.bpm.scenario.delegate.SignalEventSubscriptionDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class SignalIntermediateCatchEventTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testReceiveSignal() {

    when(scenario.actsOnSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(SignalEventSubscriptionDelegate signalEventSubscription) {
        signalEventSubscription.receiveSignal();
      }
    });

    Scenario.run(scenario).startBy("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testDoNothing() {

    when(scenario.actsOnSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(SignalEventSubscriptionDelegate signalEventSubscription) {
        // Deal with signalEventSubscription but do nothing here
      }
    });

    Scenario.run(scenario).startBy("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasStarted("SignalIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testDoNotDealWithSignalEvent() {

    Scenario.run(scenario).startBy("SignalIntermediateCatchEventTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/SignalIntermediateCatchEventTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.actsOnSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(SignalEventSubscriptionDelegate signalEventSubscription) {
        signalEventSubscription.receiveSignal();
      }
    });

    when(otherScenario.actsOnSignalIntermediateCatchEvent("SignalIntermediateCatchEvent")).thenReturn(new SignalIntermediateCatchEventAction() {
      @Override
      public void execute(SignalEventSubscriptionDelegate signalEventSubscription) {
      }
    });

    Scenario.run(otherScenario).startBy("SignalIntermediateCatchEventTest").execute();
    Scenario.run(scenario).startBy("SignalIntermediateCatchEventTest").execute();

    verify(scenario, times(1)).hasFinished("SignalIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("SignalIntermediateCatchEvent");

  }

}
