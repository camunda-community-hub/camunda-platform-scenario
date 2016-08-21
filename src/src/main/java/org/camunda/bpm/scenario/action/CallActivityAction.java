package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.CallActivityWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface CallActivityAction extends ScenarioAction<CallActivityWaitstate> {

  @Override
  void execute(CallActivityWaitstate processInstance);

}
