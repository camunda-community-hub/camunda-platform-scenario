package org.camunda.bpm.scenario.test.gateways;

import org.camunda.bpm.engine.test.Deployment;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayTest extends org.camunda.bpm.scenario.test.waitstates.EventBasedGatewayTest {

  // test class to find and execute this test under both aspects 'gateways' and 'waitstates'
  // methods are necessary for Camunda BPM 7.2.0 due to a bug with the @Deployment annotation

  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testReceiveMessage() {
    super.testReceiveMessage();
  }

  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNothing() {
    super.testDoNothing();
  }

  @Deployment(resources = {"org/camunda/bpm/scenario/test/waitstates/EventBasedGatewayTest.bpmn"})
  public void testDoNotDealWithEventBasedGateway() {
    super.testDoNotDealWithEventBasedGateway();
  }

}
