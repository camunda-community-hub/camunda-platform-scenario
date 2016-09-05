package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface UserTaskAction extends Action<TaskDelegate> {

  @Override
  void execute(final TaskDelegate task) throws Exception;

}
