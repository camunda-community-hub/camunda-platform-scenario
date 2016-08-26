package org.camunda.bpm.scenario.test.gateways;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.scenario.Scenario;
import org.camunda.bpm.scenario.action.EventBasedGatewayAction;
import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;
import org.camunda.bpm.scenario.test.AbstractTest;
import org.junit.Test;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public class EventBasedGatewayTest extends org.camunda.bpm.scenario.test.waitstates.EventBasedGatewayTest {

  // test class to find and execute this test under both directories

}
