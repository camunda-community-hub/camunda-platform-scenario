package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.SignalEventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SignalIntermediateCatchEventAction extends ScenarioAction<SignalEventSubscriptionDelegate> {

  @Override
  void execute(SignalEventSubscriptionDelegate eventSubscription);

}
