package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ReceiveTaskAction extends MessageIntermediateCatchEventAction {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a receive task.
   *
   * @param message the event subscription waiting for a message or an
   * object able to signal the receive task execution to move on in case
   * you do not use the receive task with a message subscription.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final EventSubscriptionDelegate message) throws Exception;

}
