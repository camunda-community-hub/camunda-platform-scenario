package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayAction extends ScenarioAction<EventBasedGatewayDelegate> {

  @Override
  void execute(EventBasedGatewayDelegate eventBasedGateway);

}
