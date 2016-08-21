package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.ReceiveTaskWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface ReceiveTaskAction extends ScenarioAction<ReceiveTaskWaitstate> {

  @Override
  void execute(ReceiveTaskWaitstate eventSubscription);

}
