package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageIntermediateCatchEventAction extends Action<EventSubscriptionDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a message intermediate catch event.
   *
   * @param message the event subscription waiting for a message.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final EventSubscriptionDelegate message) throws Exception;

}
