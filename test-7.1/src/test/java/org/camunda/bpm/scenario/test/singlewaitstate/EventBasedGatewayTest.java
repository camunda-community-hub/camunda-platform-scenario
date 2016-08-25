package org.camunda.bpm.scenario.test.singlewaitstate;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.runner.EventBasedGatewayWaitstate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayTest extends AbstractTest {

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.atEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayWaitstate gateway) {
        gateway.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testDoNothing() {

    when(scenario.atEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayWaitstate gateway) {
        // Do nothing means process moves forward because of the timer
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayTest").execute();

    verify(scenario, times(1)).hasFinished("TimerIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {

    Scenario.run(scenario).startBy("EventBasedGatewayTest").execute();

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testToBeforeEventBasedGateway() {

    when(scenario.atEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayWaitstate gateway) {
        gateway.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayTest").toBefore("EventBasedGateway").execute();

    verify(scenario, times(1)).hasStarted("EventBasedGateway");
    verify(scenario, never()).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testToAfterEventBasedGateway() {

    when(scenario.atEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayWaitstate gateway) {
        gateway.receiveMessage();
      }
    });

    Scenario.run(scenario).startBy("EventBasedGatewayTest").toAfter("EventBasedGateway").execute();

    verify(scenario, times(1)).hasStarted("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/singlewaitstate/EventBasedGatewayTest.bpmn"})
  public void testWhileOtherProcessInstanceIsRunning() {

    when(scenario.atEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayWaitstate gateway) {
        gateway.receiveMessage();
      }
    });

    Scenario.run(otherScenario).startBy("EventBasedGatewayTest").toBefore("EventBasedGateway").execute();
    Scenario.run(scenario).startBy("EventBasedGatewayTest").execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("EndEvent");
    verify(otherScenario, never()).hasFinished("EventBasedGateway");

  }

}
