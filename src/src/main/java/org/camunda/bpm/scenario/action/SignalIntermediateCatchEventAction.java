package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalIntermediateCatchEventAction extends ScenarioAction<EventSubscriptionDelegate> {

  @Override
  void execute(final EventSubscriptionDelegate signal);

}
