package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.MessageIntermediateThrowEventWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface MessageIntermediateThrowEventAction extends ScenarioAction<MessageIntermediateThrowEventWaitstate> {

  @Override
  void execute(MessageIntermediateThrowEventWaitstate externalTask);

}
