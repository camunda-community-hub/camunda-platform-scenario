package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.MessageEventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageIntermediateCatchEventAction extends ScenarioAction<MessageEventSubscriptionDelegate> {

  @Override
  void execute(MessageEventSubscriptionDelegate eventSubscription);

}
