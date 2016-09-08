package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalIntermediateCatchEventAction extends Action<EventSubscriptionDelegate> {

  @Override
  void execute(final EventSubscriptionDelegate signal) throws Exception;

}
