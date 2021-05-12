package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author <a href="martin.schimak@plexiti.com">Martin Schimak</a>
 */
public interface SignalIntermediateCatchEventAction extends Action<EventSubscriptionDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at a signal intermediate catch event.
   *
   * @param signal the event subscription waiting for a signal.
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final EventSubscriptionDelegate signal) throws Exception;

}
