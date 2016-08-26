package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.MessageEventSubscriptionDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ReceiveTaskAction extends MessageIntermediateCatchEventAction {

  @Override
  void execute(MessageEventSubscriptionDelegate eventSubscription);

}
