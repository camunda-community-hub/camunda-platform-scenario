package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.SendTaskWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface SendTaskAction extends ScenarioAction<SendTaskWaitstate> {

  @Override
  void execute(SendTaskWaitstate externalTask);

}
