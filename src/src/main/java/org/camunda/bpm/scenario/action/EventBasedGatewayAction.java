package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.EventBasedGatewayWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayAction extends ScenarioAction<EventBasedGatewayWaitstate> {

  @Override
  void execute(EventBasedGatewayWaitstate eventBasedGateway);

}
