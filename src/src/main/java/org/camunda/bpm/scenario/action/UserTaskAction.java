package org.camunda.bpm.scenario.action;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface UserTaskAction extends ScenarioAction<TaskDelegate> {

  @Override
  void execute(final TaskDelegate task);

}
