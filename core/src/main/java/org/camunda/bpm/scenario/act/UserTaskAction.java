package org.camunda.bpm.scenario.act;

import org.camunda.bpm.scenario.delegate.TaskDelegate;

/**
 * @author Martin Schimak <martin.schimak@plexiti.com>
 */
public interface UserTaskAction extends Action<TaskDelegate> {

  /**
   * Implement this action with custom code to be executed when the
   * process instance arrives at an user task.
   *
   * @param task the user task waiting for completion by a human
   *
   * @throws Exception in case your custom code throws checked exceptions.
   * Such exceptions will be wrapped into RuntimeExceptions and rethrown.
   */
  @Override
  void execute(final TaskDelegate task) throws Exception;

}
