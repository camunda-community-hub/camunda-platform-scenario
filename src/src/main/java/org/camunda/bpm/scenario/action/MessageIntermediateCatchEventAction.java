package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.MessageIntermediateCatchEventWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageIntermediateCatchEventAction extends ScenarioAction<MessageIntermediateCatchEventWaitstate> {

  @Override
  void execute(MessageIntermediateCatchEventWaitstate eventSubscription);

}
