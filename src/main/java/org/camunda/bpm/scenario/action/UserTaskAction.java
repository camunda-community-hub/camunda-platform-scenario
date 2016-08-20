package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.runner.UserTaskWaitstate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface UserTaskAction extends ScenarioAction<UserTaskWaitstate> {

  @Override
  void execute(UserTaskWaitstate task);

}
