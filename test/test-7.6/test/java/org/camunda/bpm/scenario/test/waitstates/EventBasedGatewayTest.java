package org.camunda.bpm.scenario.test.waitstates;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.act.EventBasedGatewayAction;
import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayTest extends AbstractTest {

  @Before
  public void setCondition() {
    variables.put("condition", false);
  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testReceiveMessage() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        gateway.getEventSubscription("MessageIntermediateCatchEvent").receive();
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayTest", variables).execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, times(1)).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, never()).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testConditionInitiallyTrue() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        //
      }
    });


    variables.put("condition", true);
    Scenario.run(scenario).startByKey("EventBasedGatewayTest", variables).execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

    verify(scenario, never()).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testConditionInitiallyFalseThenSetConditionTrue() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        rule.getRuntimeService().setVariable(gateway.getProcessInstance().getId(), "condition", true);
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayTest", variables).execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNothing() {

    when(scenario.waitsAtEventBasedGateway("EventBasedGateway")).thenReturn(new EventBasedGatewayAction() {
      @Override
      public void execute(EventBasedGatewayDelegate gateway) {
        // Do nothing means process moves forward because of the timers
      }
    });

    Scenario.run(scenario).startByKey("EventBasedGatewayTest", variables).execute();

    verify(scenario, times(1)).hasFinished("EventBasedGateway");
    verify(scenario, never()).hasFinished("MessageIntermediateCatchEvent");
    verify(scenario, never()).hasFinished("ConditionalIntermediateEvent");
    verify(scenario, times(1)).hasFinished("TimerIntermediateEvent");
    verify(scenario, times(1)).hasFinished("EndEvent");

  }

  @Test(expected=AssertionError.class)
  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {

    Scenario.run(scenario).startByKey("EventBasedGatewayTest", variables).execute();

  }

}
