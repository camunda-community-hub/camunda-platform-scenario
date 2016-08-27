package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.EventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageIntermediateCatchEventAction extends ScenarioAction<EventSubscriptionDelegate> {

  @Override
  void execute(EventSubscriptionDelegate messageEvent);

}
