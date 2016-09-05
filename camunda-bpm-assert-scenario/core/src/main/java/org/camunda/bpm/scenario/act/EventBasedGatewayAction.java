package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayAction extends Action<EventBasedGatewayDelegate> {

  @Override
  void execute(final EventBasedGatewayDelegate gateway) throws Exception;

}
