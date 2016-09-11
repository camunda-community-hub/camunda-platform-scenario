package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventBasedGatewayDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface EventBasedGatewayAction extends Action<EventBasedGatewayDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at an event based gateway.
   *
   * @param gateway object providing you with further details about
   * the events or receive tasks followed by this gateway.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final EventBasedGatewayDelegate gateway) throws Exception;

}
